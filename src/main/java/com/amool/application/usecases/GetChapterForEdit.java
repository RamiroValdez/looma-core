package com.amool.application.usecases;

import com.amool.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.adapters.in.rest.mappers.ChapterMapper;
import com.amool.adapters.in.rest.mappers.LanguageMapper;
import com.amool.application.port.out.LoadChapterContentPort;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.LoadLanguagePort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.Language;
import com.amool.domain.model.Work;

import java.util.List;
import java.util.Optional;

public class GetChapterForEdit {

    private final LoadChapterPort loadChapterPort;
    private final LoadChapterContentPort loadChapterContentPort;
    private final LoadLanguagePort loadLanguagePort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;

    public GetChapterForEdit(
            LoadChapterPort loadChapterPort,
            LoadChapterContentPort loadChapterContentPort,
            LoadLanguagePort loadLanguagePort,
            ObtainWorkByIdPort obtainWorkByIdPort
    ) {
        this.loadChapterPort = loadChapterPort;
        this.loadChapterContentPort = loadChapterContentPort;
        this.loadLanguagePort = loadLanguagePort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
    }

    public Optional<ChapterResponseDto> execute(Long chapterId, String language) {
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
                            workIdStr,
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
}
