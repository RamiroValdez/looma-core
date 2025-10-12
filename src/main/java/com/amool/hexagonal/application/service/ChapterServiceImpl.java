package com.amool.hexagonal.application.service;

import com.amool.hexagonal.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.hexagonal.adapters.in.rest.dtos.UpdateChapterRequest;
import com.amool.hexagonal.adapters.in.rest.mappers.ChapterMapper;
import com.amool.hexagonal.adapters.in.rest.mappers.LanguageMapper;
import com.amool.hexagonal.application.port.in.ChapterService;
import com.amool.hexagonal.application.port.out.*;
import com.amool.hexagonal.domain.model.Chapter;
import com.amool.hexagonal.domain.model.Language;
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
    private final UpdateChapterPort updateChapterPort;
    private final SaveChapterContentPort saveChapterContentPort;
    private final DeleteChapterPort deleteChapterPort;
    private final DeleteChapterContentPort deleteChapterContentPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final LoadLanguagePort loadLanguagePort;

    @PersistenceContext
    private EntityManager entityManager;

    public ChapterServiceImpl(LoadChapterPort loadChapterPort,
                              LoadChapterContentPort loadChapterContentPort,
                              SaveChapterPort saveChapterPort,
                              UpdateChapterPort updateChapterPort,
                              SaveChapterContentPort saveChapterContentPort,
                              DeleteChapterPort deleteChapterPort,
                              DeleteChapterContentPort deleteChapterContentPort,
                              ObtainWorkByIdPort obtainWorkByIdPort,
                                LoadLanguagePort loadLanguagePort
                              ) {
        this.loadChapterPort = loadChapterPort;
        this.loadChapterContentPort = loadChapterContentPort;
        this.saveChapterPort = saveChapterPort;
        this.updateChapterPort = updateChapterPort;
        this.saveChapterContentPort = saveChapterContentPort;
        this.deleteChapterPort = deleteChapterPort;
        this.deleteChapterContentPort = deleteChapterContentPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.loadLanguagePort = loadLanguagePort;
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

            String languageCode = this.loadLanguagePort.loadLanguageById(languageId)
                    .map(Language::getCode)
                    .orElse("es");

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

    @Override
    public Optional<ChapterResponseDto> getChapterForEdit(Long chapterId, String language) {
        return loadChapterPort.loadChapterForEdit(chapterId)
                .map(chapter -> {

                    String workIdStr = chapter.getWorkId().toString();
                    String chapterIdStr = chapterId.toString();

                    List<String> availableCodeLanguages = loadChapterContentPort.getAvailableLanguages(workIdStr, chapterIdStr);

                    List<Language> availableLanguages = loadLanguagePort.getLanguagesByCodes(availableCodeLanguages);

                    String content = loadChapterContentPort
                            .loadContent(workIdStr, chapterIdStr, language)
                            .map(chapterContent -> chapterContent.getContent(language))
                            .orElseGet(() -> loadChapterContentPort.loadContent(workIdStr, chapterIdStr)
                                    .map(chapterContent -> chapterContent.getContent(language))
                                    .orElse(""));

                    Optional<Work> workOptional = obtainWorkByIdPort.obtainWorkById(chapter.getWorkId());

                    Language defaultLanguageCode = workOptional.map(Work::getOriginalLanguage)
                            .orElse(availableLanguages.getFirst());

                    String workName = workOptional
                            .map(Work::getTitle)
                            .orElse("Obra desconocida");

                    Integer chapterNumber = workOptional
                            .map(work -> calculateChapterNumber(work, chapter))
                            .orElse(null);

                    return ChapterMapper.toDto(
                            chapter,
                            content,
                            workName,
                            LanguageMapper.toDtoList(availableLanguages),
                            chapterNumber,
                            LanguageMapper.toDto(defaultLanguageCode)
                    );
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

    @Override
    public boolean updateChapter(Long chapterId, UpdateChapterRequest updateRequest) {
        return loadChapterPort.loadChapterForEdit(chapterId)
                .map(existingChapter -> {
                    applyUpdates(existingChapter, updateRequest);
                    Optional<Chapter> updatedChapter = updateChapterPort.updateChapter(existingChapter);
                    updatedChapter.ifPresent(chapter -> updateVersionsIfNeeded(chapter, updateRequest));
                    return updatedChapter.isPresent();
                })
                .orElse(false);
    }

    private void applyUpdates(Chapter chapter, UpdateChapterRequest updateRequest) {
        if (updateRequest.getTitle() != null) {
            chapter.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getStatus() != null) {
            chapter.setPublicationStatus(updateRequest.getStatus());
        }
        if (updateRequest.getPrice() != null) {
            chapter.setPrice(updateRequest.getPrice());
        }
        if (updateRequest.getLast_update() != null) {
            chapter.setLastModified(updateRequest.getLast_update());
        }
        if (updateRequest.getAllow_ai_translation() != null) {
            chapter.setAllowAiTranslation(updateRequest.getAllow_ai_translation());
        }
    }

    private void updateVersionsIfNeeded(Chapter savedChapter, UpdateChapterRequest updateRequest) {
        if (updateRequest.getVersions() == null || updateRequest.getVersions().isEmpty()) {
            return;
        }
        String workId = savedChapter.getWorkId() != null ? savedChapter.getWorkId().toString() : null;
        String chapterId = savedChapter.getId() != null ? savedChapter.getId().toString() : null;
        if (workId == null || chapterId == null) {
            return;
        }

        updateRequest.getVersions().forEach((languageCode, content) ->
                saveChapterContentPort.saveContent(workId, chapterId, languageCode, content != null ? content : "")
        );
    }
}
