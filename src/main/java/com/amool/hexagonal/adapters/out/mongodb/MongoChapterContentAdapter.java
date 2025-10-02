package com.amool.hexagonal.adapters.out.mongodb;

import com.amool.hexagonal.adapters.in.rest.dto.UpdateChapterContentRequest;
import com.amool.hexagonal.adapters.out.mongodb.document.ChapterContentDocument;
import com.amool.hexagonal.adapters.out.mongodb.repository.MongoChapterContentRepository;
import com.amool.hexagonal.application.port.out.LoadChapterContentPort;
import com.amool.hexagonal.application.port.out.SaveChapterContentPort;
import com.amool.hexagonal.domain.model.ChapterContent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class MongoChapterContentAdapter implements LoadChapterContentPort, SaveChapterContentPort {

    private final MongoChapterContentRepository repository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<ChapterContent> loadContent(String workId, String chapterId) {
        return repository.findByWorkIdAndChapterId(workId, chapterId)
                .map(this::toDomain);
    }

    @Override
    public Optional<ChapterContent> loadContent(String workId, String chapterId, String language) {
        return repository.findByWorkIdAndChapterId(workId, chapterId)
                .map(doc -> {
                    if (language != null && !language.isEmpty()) {
                        Map<String, String> contentByLanguage = doc.getContentByLanguage();
                        String defaultLang = doc.getDefaultLanguage() != null ? doc.getDefaultLanguage() : "es";
                        String content = contentByLanguage.getOrDefault(language, 
                            contentByLanguage.getOrDefault(defaultLang, ""));
                        
                        return new ChapterContent(
                            doc.getWorkId(),
                            doc.getChapterId(),
                            Map.of(language, content),
                            language
                        );
                    }
                    return toDomain(doc);
                });
    }

    @Override
    public List<String> getAvailableLanguages(String workId, String chapterId) {
        return repository.findByWorkIdAndChapterId(workId, chapterId)
                .map(ChapterContentDocument::getContentByLanguage)
                .map(Map::keySet)
                .map(ArrayList::new)
                .orElse(new ArrayList<>());
    }

    @Override
    public ChapterContent saveContent(String workId, String chapterId, String language, String content) {
        Query query = new Query(
            Criteria.where("workId").is(workId)
                .and("chapterId").is(chapterId)
        );
        
        Update update = new Update()
            .set("workId", workId)
            .set("chapterId", chapterId)
            .set("contentByLanguage." + language, content);
            
        if (!mongoTemplate.exists(query, ChapterContentDocument.class)) {
            update.set("defaultLanguage", language);
        }
        
        mongoTemplate.upsert(query, update, ChapterContentDocument.class);
        
        return loadContent(workId, chapterId, language)
                .orElseThrow(() -> new RuntimeException("Failed to save content"));
    }

    private ChapterContent toDomain(ChapterContentDocument document) {
        if (document == null) {
            return null;
        }
        return new ChapterContent(
            document.getWorkId(),
            document.getChapterId(),
            document.getContentByLanguage(),
            document.getDefaultLanguage()
        );
    }
}
