package com.amool.adapters.out.persistence;

import com.amool.domain.model.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TagPersistenceAdapterTest {

    @Autowired
    private TagPersistenceAdapter adapter;

    @Test
    @DisplayName("createTag persiste y retorna el id, y luego searchTag lo encuentra")
    void createTag_and_search_found() {
        String tagName = "fantasia";

        Long id = adapter.createTag(tagName);

        assertThat(id).isNotNull();
        Optional<Tag> found = adapter.searchTag(tagName);
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(id);
        assertThat(found.get().getName()).isEqualTo(tagName);
    }

    @Test
    @DisplayName("searchTag retorna Optional.empty cuando no existe")
    void searchTag_not_found() {
        String missing = "no-existe";

        Optional<Tag> result = adapter.searchTag(missing);

        assertThat(result).isEmpty();
    }
}
