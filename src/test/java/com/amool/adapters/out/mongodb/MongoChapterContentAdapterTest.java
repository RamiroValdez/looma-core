package com.amool.adapters.out.mongodb;

import com.amool.adapters.out.mongodb.document.ChapterContentDocument;
import com.amool.adapters.out.mongodb.repository.MongoChapterContentRepository;
import com.amool.domain.model.ChapterContent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataMongoTest
@Import(MongoChapterContentAdapter.class)
public class MongoChapterContentAdapterTest {

    @Autowired
    private MongoChapterContentAdapter adapter;

    @Autowired
    private MongoChapterContentRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String COLLECTION = "chapter_contents";

    @BeforeEach
    void setup() {
        mongoTemplate.dropCollection(COLLECTION);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(COLLECTION);
    }

    @Test
    void saveContent_firstInsert_setsDefaultLanguageAndPersists() {
        String workId = "work-1";
        String chapterId = "ch-1";

        ChapterContent saved = adapter.saveContent(workId, chapterId, "es", "Hola mundo");

        assertThat(saved.getWorkId()).isEqualTo(workId);
        assertThat(saved.getChapterId()).isEqualTo(chapterId);
        assertThat(saved.getDefaultLanguage()).isEqualTo("es");
        assertThat(saved.getContent("es")).isEqualTo("Hola mundo");
        assertThat(saved.getContentByLanguage()).containsEntry("es", "Hola mundo");

        // Carga sin idioma debe devolver el documento completo
        ChapterContent loaded = adapter.loadContent(workId, chapterId).orElseThrow();
        assertThat(loaded.getDefaultLanguage()).isEqualTo("es");
        assertThat(loaded.getContentByLanguage()).containsEntry("es", "Hola mundo");
    }

    @Test
    void saveContent_secondLanguage_keepsDefaultAndAddsNewLanguage() {
        String workId = "work-2";
        String chapterId = "ch-2";

        adapter.saveContent(workId, chapterId, "es", "Contenido ES");
        adapter.saveContent(workId, chapterId, "en", "Content EN");

        ChapterContent full = adapter.loadContent(workId, chapterId).orElseThrow();
        assertThat(full.getDefaultLanguage()).isEqualTo("es");
        assertThat(full.getContentByLanguage()).containsEntry("es", "Contenido ES")
                                               .containsEntry("en", "Content EN");

        ChapterContent onlyEn = adapter.loadContent(workId, chapterId, "en").orElseThrow();
        assertThat(onlyEn.getDefaultLanguage()).isEqualTo("en");
        assertThat(onlyEn.getContentByLanguage()).containsOnly(Map.entry("en", "Content EN"));
    }

    @Test
    void loadContent_withRequestedLanguageNotPresent_fallsBackToDefault() {
        String workId = "work-3";
        String chapterId = "ch-3";

        adapter.saveContent(workId, chapterId, "es", "Base ES");

        ChapterContent fr = adapter.loadContent(workId, chapterId, "fr").orElseThrow();
        assertThat(fr.getDefaultLanguage()).isEqualTo("fr");
        assertThat(fr.getContentByLanguage()).containsOnly(Map.entry("fr", "Base ES"));
    }

    @Test
    void loadContent_whenDocumentHasNoDefaultLanguage_fallsBackToEsKey() {
        String workId = "work-4";
        String chapterId = "ch-4";

        ChapterContentDocument doc = new ChapterContentDocument();
        doc.setWorkId(workId);
        doc.setChapterId(chapterId);
        doc.setContentByLanguage(Map.of("es", "Hola"));

        repository.save(doc);

        ChapterContent fr = adapter.loadContent(workId, chapterId, "fr").orElseThrow();
        assertThat(fr.getDefaultLanguage()).isEqualTo("fr");

        assertThat(fr.getContentByLanguage()).containsOnly(Map.entry("fr", "Hola"));
    }

    @Test
    void getAvailableLanguages_returnsAllKeys() {
        String workId = "work-5";
        String chapterId = "ch-5";

        adapter.saveContent(workId, chapterId, "es", "Uno");
        adapter.saveContent(workId, chapterId, "en", "Two");
        adapter.saveContent(workId, chapterId, "pt", "Tres");

        var languages = adapter.getAvailableLanguages(workId, chapterId);
        assertThat(languages).hasSize(3);
        assertThat(Set.copyOf(languages)).isEqualTo(Set.of("es", "en", "pt"));
    }

    @Test
    void deleteContent_removesDocument() {
        String workId = "work-6";
        String chapterId = "ch-6";

        adapter.saveContent(workId, chapterId, "es", "Para borrar");
        assertThat(adapter.loadContent(workId, chapterId)).isPresent();

        adapter.deleteContent(workId, chapterId);

        assertThat(adapter.loadContent(workId, chapterId)).isEmpty();
        assertThat(adapter.getAvailableLanguages(workId, chapterId)).isEmpty();
    }
}
