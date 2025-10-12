package com.amool.hexagonal.application.service;

import com.amool.hexagonal.adapters.in.rest.dtos.ChapterResponseDto;
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
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
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
    private final LoadLanguagePort loadLanguagePort;
    private final UpdateChapterStatusPort updateChapterStatusPort;

    @PersistenceContext
    private EntityManager entityManager;

    public ChapterServiceImpl(LoadChapterPort loadChapterPort,
                              LoadChapterContentPort loadChapterContentPort,
                              SaveChapterPort saveChapterPort,
                              SaveChapterContentPort saveChapterContentPort,
                              DeleteChapterPort deleteChapterPort,
                              DeleteChapterContentPort deleteChapterContentPort,
                              ObtainWorkByIdPort obtainWorkByIdPort,
                              LoadLanguagePort loadLanguagePort,
                              UpdateChapterStatusPort updateChapterStatusPort
                              ) {
        this.loadChapterPort = loadChapterPort;
        this.loadChapterContentPort = loadChapterContentPort;
        this.saveChapterPort = saveChapterPort;
        this.saveChapterContentPort = saveChapterContentPort;
        this.deleteChapterPort = deleteChapterPort;
        this.deleteChapterContentPort = deleteChapterContentPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.loadLanguagePort = loadLanguagePort;
        this.updateChapterStatusPort = updateChapterStatusPort;
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
        Chapter chapter = loadChapterPort.loadChapter(workId, chapterId)
                .orElseThrow(() -> new NoSuchElementException("Capítulo no encontrado"));

        if (!"DRAFT".equals(chapter.getPublicationStatus())) {
            throw new IllegalStateException("Solo se puede eliminar un capítulo en estado DRAFT");
        }

        deleteChapterContentPort.deleteContent(workId.toString(), chapterId.toString());
        deleteChapterPort.deleteChapter(workId, chapterId);
    }

    @Override
    public void publishChapter(Long workId, Long chapterId, Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("Usuario no autenticado");
        }

        Work work = obtainWorkByIdPort.obtainWorkById(workId)
                .orElseThrow(() -> new NoSuchElementException("Obra no encontrada"));
        if (work.getCreator() == null || !authenticatedUserId.equals(work.getCreator().getId())) {
            throw new SecurityException("No autorizado para modificar esta obra");
        }

        Chapter chapter = loadChapterPort.loadChapter(workId, chapterId)
                .orElseThrow(() -> new NoSuchElementException("Capítulo no encontrado"));

        if (!"DRAFT".equals(chapter.getPublicationStatus())) {
            throw new IllegalStateException("Solo se puede publicar un capítulo en estado DRAFT");
        }

        updateChapterStatusPort.updatePublicationStatus(workId, chapterId, "PUBLISHED", LocalDateTime.now());
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
    public void schedulePublication(Long workId, Long chapterId, Instant when, Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("Usuario no autenticado");
        }
        if (when == null || !when.isAfter(Instant.now())) {
            throw new IllegalArgumentException("Fecha/Hora inválida para programar publicación");
        }

        Work work = obtainWorkByIdPort.obtainWorkById(workId)
                .orElseThrow(() -> new NoSuchElementException("Obra no encontrada"));
        
        if (work.getCreator() == null || !authenticatedUserId.equals(work.getCreator().getId())) {
            throw new SecurityException("No autorizado para modificar esta obra");
        }

        Chapter chapter = loadChapterPort.loadChapter(workId, chapterId)
                .orElseThrow(() -> new NoSuchElementException("Capítulo no encontrado"));

        if (!"DRAFT".equals(chapter.getPublicationStatus())) {
            throw new IllegalStateException("Solo se puede programar publicación si el capítulo está en estado DRAFT");
        }

        updateChapterStatusPort.schedulePublication(workId, chapterId, when);
    }

    @Override
    public void cancelScheduledPublication(Long workId, Long chapterId, Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("Usuario no autenticado");
        }
        Work work = obtainWorkByIdPort.obtainWorkById(workId)
                .orElseThrow(() -> new NoSuchElementException("Obra no encontrada"));
        
        if (work.getCreator() == null || !authenticatedUserId.equals(work.getCreator().getId())) {
            throw new SecurityException("No autorizado para modificar esta obra");
        }
        Chapter chapter = loadChapterPort.loadChapter(workId, chapterId)
                .orElseThrow(() -> new NoSuchElementException("Capítulo no encontrado"));

        if (!"SCHEDULED".equals(chapter.getPublicationStatus())) {
            throw new IllegalStateException("Solo se puede cancelar la programación si el capítulo está en estado SCHEDULED");
        }

        updateChapterStatusPort.clearSchedule(workId, chapterId);
    }
}
