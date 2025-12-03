package com.amool.adapters.out.persistence;

import com.amool.domain.model.AnalyticsRetention;
import com.amool.domain.model.ReadingHistory;
import com.amool.adapters.out.persistence.entity.ReadingHistoryEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AnalyticsPersistenceAdapterTest {

    @Autowired
    private AnalyticsPersistenceAdapter adapter;

    @Autowired
    private EntityManager entityManager;

    private List<AnalyticsRetention> retentionResult;
    private List<ReadingHistory> readingHistoryResult;

    @BeforeEach
    void cleanData() {
        entityManager.createQuery("DELETE FROM ReadingHistoryEntity").executeUpdate();
    }

//    @Test
//    void should_aggregate_distinct_users_per_chapter_ordered_by_first_read() {
//        givenReading(1000L, 1L, 10L, LocalDateTime.parse("2024-01-01T10:00:00"));
//        givenReading(1000L, 1L, 11L, LocalDateTime.parse("2024-01-01T11:00:00"));
//        givenReading(1000L, 2L, 12L, LocalDateTime.parse("2024-01-02T12:00:00"));
//        givenReading(1000L, 1L, 10L, LocalDateTime.parse("2024-01-03T13:00:00"));
//
//        whenGetRetentionTotalsPerChapter(1000L);
//
//        thenRetentionHasSize(2);
//        thenRetentionChapterHasTotal(0, 1L, 2L);
//        thenRetentionChapterHasTotal(1, 2L, 1L);
//    }

    @Test
    void should_return_reading_history_for_chapter_mapped_to_domain() {
        givenReading(7L);
        givenReading(7L);
        givenReading(2L);

        whenGetReadingHistory(7L);

        thenReadingHistoryHasSize(2, 7L);

    }

    private void givenReading(Long chapterId) {
        ReadingHistoryEntity e = new ReadingHistoryEntity();
        e.setWorkId(1000L);
        e.setChapterId(chapterId);
        e.setUserId(21L);
        e.setReadAt(LocalDateTime.parse("2024-02-01T08:00:00"));
        entityManager.persist(e);
        entityManager.flush();
    }

    private void whenGetRetentionTotalsPerChapter(Long workId) {
        retentionResult = adapter.getRetentionTotalsPerChapter(workId);
    }

    private void whenGetReadingHistory(Long chapterId) {
        readingHistoryResult = adapter.getReadingHistory(chapterId);
    }

    private void thenRetentionHasSize(int expected) {
        assertThat(retentionResult).hasSize(expected);
    }

    private void thenRetentionChapterHasTotal(int index, Long expectedChapter, Long expectedTotal) {
        AnalyticsRetention item = retentionResult.get(index);
        assertThat(item.getChapter()).isEqualTo(expectedChapter);
        assertThat(item.getTotalReaders()).isEqualTo(expectedTotal);
    }

    private void thenReadingHistoryHasSize(int expectedSize, Long expectedChapterId) {
        assertThat(readingHistoryResult).hasSize(expectedSize);
        assertThat( readingHistoryResult.stream().filter(
                it -> it.getChapterId().equals(expectedChapterId)).count()).isEqualTo(expectedSize);
    }

    private void thenReadingHistoryEntryMatches(int index, Long expectedWorkId, Long expectedChapterId) {
        ReadingHistory item = readingHistoryResult.get(index);
        assertThat(item.getWorkId()).isEqualTo(expectedWorkId);
        assertThat(item.getChapterId()).isEqualTo(expectedChapterId);
    }

    private void thenReadingHistoryContainsDates(LocalDateTime... dates) {
        assertThat(readingHistoryResult.stream().map(ReadingHistory::getReadAt))
                .containsExactlyInAnyOrder(dates);
    }
}
