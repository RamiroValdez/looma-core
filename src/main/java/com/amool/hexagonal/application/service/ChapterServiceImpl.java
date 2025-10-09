package com.amool.hexagonal.application.service;

import com.amool.hexagonal.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.hexagonal.adapters.in.rest.mappers.ChapterMapper;
import com.amool.hexagonal.application.port.in.ChapterService;
import com.amool.hexagonal.application.port.out.LoadChapterContentPort;
import com.amool.hexagonal.application.port.out.LoadChapterPort;
import com.amool.hexagonal.application.port.out.SaveChapterPort;
import com.amool.hexagonal.application.port.out.SaveChapterContentPort;
import com.amool.hexagonal.application.port.out.ObtainWorkByIdPort;
import com.amool.hexagonal.domain.model.Chapter;
import com.amool.hexagonal.adapters.out.persistence.entity.LanguageEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChapterServiceImpl implements ChapterService {

    private final LoadChapterPort loadChapterPort;
    private final LoadChapterContentPort loadChapterContentPort;
    private final SaveChapterPort saveChapterPort;
    private final SaveChapterContentPort saveChapterContentPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;

    @PersistenceContext
    private EntityManager entityManager;

    public ChapterServiceImpl(LoadChapterPort loadChapterPort,
                              LoadChapterContentPort loadChapterContentPort,
                              SaveChapterPort saveChapterPort,
                              SaveChapterContentPort saveChapterContentPort,
                              ObtainWorkByIdPort obtainWorkByIdPort) {
        this.loadChapterPort = loadChapterPort;
        this.loadChapterContentPort = loadChapterContentPort;
        this.saveChapterPort = saveChapterPort;
        this.saveChapterContentPort = saveChapterContentPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
    }

    @Override
    public Optional<ChapterWithContent> getChapterWithContent(Long bookId, Long chapterId, String language) {
        return loadChapterPort.loadChapter(bookId, chapterId)
                .flatMap(chapter -> loadChapterContentPort
                        .loadContent(bookId.toString(), chapterId.toString(), language)
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
    public Optional<ChapterResponseDto> getChapterForEdit(Long chapterId) {
        return loadChapterPort.loadChapterForEdit(chapterId)
                .map(chapter -> {
                    String workIdStr = chapter.getWorkId().toString();
                    String chapterIdStr = chapterId.toString();

                    String languageCode = getLanguageCodeFromLanguageId(chapter.getLanguageId());

                    String content = loadChapterContentPort
                            .loadContent(workIdStr, chapterIdStr, languageCode)
                            .map(chapterContent -> chapterContent.getContent(languageCode))
                            .orElseGet(() -> loadChapterContentPort.loadContent(workIdStr, chapterIdStr)
                                    .map(chapterContent -> chapterContent.getContent(languageCode))
                                    .orElse("")
                            );

                    String workName = obtainWorkByIdPort.obtainWorkById(chapter.getWorkId())
                            .map(work -> work.getTitle())
                            .orElse("Obra desconocida");

                    List<String> availableLanguages = loadChapterContentPort.getAvailableLanguages(workIdStr, chapterIdStr);

                    return ChapterMapper.toDto(chapter, content, workName, availableLanguages);
                });
    }
}
