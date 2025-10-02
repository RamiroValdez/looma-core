package com.amool.hexagonal.adapters.out.mongodb.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "chapter_contents")
@Data
public class ChapterContentDocument {
    @Id
    private String id;
    private String workId;
    private String chapterId;
    private Map<String, String> contentByLanguage;
    private String defaultLanguage;
}
