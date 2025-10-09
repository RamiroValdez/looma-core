package com.amool.hexagonal.application.service;

import com.amool.hexagonal.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.hexagonal.adapters.in.rest.mappers.ChapterMapper;
import com.amool.hexagonal.application.port.in.ChapterService;
import com.amool.hexagonal.application.port.out.LoadChapterContentPort;
import com.amool.hexagonal.application.port.out.LoadChapterPort;
import com.amool.hexagonal.application.port.out.SaveChapterPort;
import com.amool.hexagonal.application.port.out.SaveChapterContentPort;
import com.amool.hexagonal.application.port.out.DeleteChapterPort;
import com.amool.hexagonal.application.port.out.DeleteChapterContentPort;
import com.amool.hexagonal.application.port.out.ObtainWorkByIdPort;
import com.amool.hexagonal.domain.model.Chapter;
import com.amool.hexagonal.adapters.out.persistence.entity.LanguageEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import com.amool.hexagonal.domain.model.Work;

@Service
@Transactional
public class ChapterServiceImpl implements ChapterService {

    private final LoadChapterPort loadChapterPort;
    private final LoadChapterContentPort loadChapterContentPort;
    private final SaveChapterPort saveChapterPort;
    private final SaveChapterContentPort saveChapterContentPort;
    private final DeleteChapterPort deleteChapterPort;
    private final DeleteChapterContentPort deleteChapterContentPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;

    @PersistenceContext
    private EntityManager entityManager;

    public ChapterServiceImpl(LoadChapterPort loadChapterPort,
                              LoadChapterContentPort loadChapterContentPort,
                              SaveChapterPort saveChapterPort,
                              SaveChapterContentPort saveChapterContentPort,
                              DeleteChapterPort deleteChapterPort,
                              DeleteChapterContentPort deleteChapterContentPort) {
                              ObtainWorkByIdPort obtainWorkByIdPort) {
        this.loadChapterPort = loadChapterPort;
        this.loadChapterContentPort = loadChapterContentPort;
        this.saveChapterPort = saveChapterPort;
        this.saveChapterContentPort = saveChapterContentPort;
        this.deleteChapterPort = deleteChapterPort;
        this.deleteChapterContentPort = deleteChapterContentPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
    }

    @Override
    public Optional<ChapterWithContent> getChapterWithContent(Long workId, Long chapterId, String language) {
        return loadChapterPort.loadChapter(workId, chapterId)
                .flatMap(chapter -> loadChapterContentPort
                        .loadContent(workId.toString(), chapterId.toString(), language)
                        .map(content -> new ChapterWithContent(chapter, content.getContent(language))));
    }

    @Override
    public Chapter createEmptyChapter(Long workId, Long languageId, String contentType) {
        Chapter chapter = new Chapter();
        chapter.setWorkId(workId);
        chapter.setLanguageId(languageId);

        Chapter savedChapter = saveChapterPort.saveChapter(chapter);

        if ("TEXT".equals(contentType)) {
            String languageCode = getLanguageCodeFromLanguageId(languageId);
            String emptyContent = ""; 
            saveChapterContentPort.saveContent(
                workId.toString(),
                savedChapter.getId().toString(),
                languageCode,
                emptyContent
            );
        }

        return savedChapter;
    }

    @Override
    public void deleteChapter(Long workId, Long chapterId) {
        deleteChapterContentPort.deleteContent(workId.toString(), chapterId.toString());
        deleteChapterPort.deleteChapter(workId, chapterId);
    }

    private String getLanguageCodeFromLanguageId(Long languageId) {
        if (languageId == null) {
            return "es";
        }

        LanguageEntity languageEntity = entityManager.find(LanguageEntity.class, languageId);
        if (languageEntity == null) {
            return "es"; 
        }

        String languageName = languageEntity.getName().toLowerCase().trim();

        return switch (languageName) {
            case "español", "spanish" -> "es";
            case "english", "inglés" -> "en";
            case "french", "francés" -> "fr";
            case "german", "alemán" -> "de";
            case "italian", "italiano" -> "it";
            case "portuguese", "portugués" -> "pt";
            default -> "es"; 
        };
    }

    @Override
    public Optional<ChapterResponseDto> getChapterForEdit(Long chapterId, String language) {
        return loadChapterPort.loadChapterForEdit(chapterId)
                .map(chapter -> {
                    String workIdStr = chapter.getWorkId().toString();
                    String chapterIdStr = chapterId.toString();

                    List<String> availableLanguages = loadChapterContentPort.getAvailableLanguages(workIdStr, chapterIdStr);

                    String defaultLanguageCode = getLanguageCodeFromLanguageId(chapter.getLanguageId());

                    String computedLanguage = (language != null && !language.isBlank())
                            ? language.trim().toLowerCase()
                            : defaultLanguageCode;

                    if (computedLanguage == null || computedLanguage.isBlank()) {
                        computedLanguage = defaultLanguageCode;
                    }

                    final String selectedLanguage = computedLanguage;

                    String content = loadChapterContentPort
                            .loadContent(workIdStr, chapterIdStr, selectedLanguage)
                            .map(chapterContent -> chapterContent.getContent(selectedLanguage))
                            .orElseGet(() -> loadChapterContentPort.loadContent(workIdStr, chapterIdStr)
                                    .map(chapterContent -> chapterContent.getContent(selectedLanguage))
                                    .orElse(""));

                    Optional<Work> workOptional = obtainWorkByIdPort.obtainWorkById(chapter.getWorkId());

                    String workName = workOptional
                            .map(Work::getTitle)
                            .orElse("Obra desconocida");

                    Integer chapterNumber = workOptional
                            .map(work -> calculateChapterNumber(work, chapter))
                            .orElse(null);

                    return ChapterMapper.toDto(chapter, content, workName, availableLanguages, chapterNumber, defaultLanguageCode);
                });
    }

    private Integer calculateChapterNumber(Work work, Chapter chapter) {
        if (work == null || chapter == null) {
            return null;
        }
        List<Chapter> chapters = work.getChapters();
        if (chapters == null || chapter.getId() == null) {
            return null;
        }
        for (int i = 0; i < chapters.size(); i++) {
            Chapter workChapter = chapters.get(i);
            if (workChapter != null && chapter.getId().equals(workChapter.getId())) {
                return i + 1;
            }
        }
        return null;
    }
}
