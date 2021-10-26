package com.cupcake.learning.exam.base.repository.postgres;

import com.cupcake.learning.exam.base.model.entity.postgres.PublishedExamMetaData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaRepositories
class PublishedExamMetaDataRepositoryTest {
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 10);
    private static final UUID AUTHOR_ID_ALPHA = UUID.randomUUID();
    private static final UUID AUTHOR_ID_BETA = UUID.randomUUID();

    private static final UUID EXAM_ID = UUID.randomUUID();
    private static final UUID PUBLISHED_EXAM_ID_ALPHA = UUID.randomUUID();
    private static final UUID PUBLISHED_EXAM_ID_BETA = UUID.randomUUID();
    private static final UUID PUBLISHED_EXAM_ID_CHARLIE = UUID.randomUUID();
    private static final UUID PUBLISHED_EXAM_ID_DELTA = UUID.randomUUID();
    private static final UUID INACTIVE_PUBLISHED_EXAM_ID = UUID.randomUUID();

    private static final OffsetDateTime DATE_TIME = OffsetDateTime.now();
    private static final Map<UUID, OffsetDateTime> PUBLISHED_DATE_TIME = Map.of(
            PUBLISHED_EXAM_ID_ALPHA, DATE_TIME,
            PUBLISHED_EXAM_ID_BETA, DATE_TIME.plusDays(1),
            PUBLISHED_EXAM_ID_CHARLIE, DATE_TIME.plusDays(3),
            PUBLISHED_EXAM_ID_DELTA, DATE_TIME.plusDays(2),
            INACTIVE_PUBLISHED_EXAM_ID, DATE_TIME.plusDays(2));

    @Autowired
    private PublishedExamMetaDataRepository publishedExamMetaDataRepository;

    @BeforeEach
    void setUp() {
        addPublishedExamMetaData(INACTIVE_PUBLISHED_EXAM_ID, AUTHOR_ID_ALPHA, false);
        addPublishedExamMetaData(PUBLISHED_EXAM_ID_ALPHA, AUTHOR_ID_ALPHA, true);
        addPublishedExamMetaData(PUBLISHED_EXAM_ID_BETA, AUTHOR_ID_ALPHA, true);
        addPublishedExamMetaData(PUBLISHED_EXAM_ID_CHARLIE, AUTHOR_ID_ALPHA, true);
        addPublishedExamMetaData(PUBLISHED_EXAM_ID_DELTA, AUTHOR_ID_BETA, true);
    }

    private void addPublishedExamMetaData(UUID publishedExamId, UUID authorId, boolean active) {
        var metaData = new PublishedExamMetaData();
        metaData.setPublishedExamId(publishedExamId);
        metaData.setAuthorId(authorId);
        metaData.setActive(active);
        metaData.setPublishedDateTime(PUBLISHED_DATE_TIME.get(publishedExamId));
        metaData.setExamId(EXAM_ID);

        publishedExamMetaDataRepository.save(metaData);
    }

    @Test
    void shouldReturnRecord_whenFindingByPublishedExamIdAndAuthor() {
        var metaData = publishedExamMetaDataRepository.findByPublishedExamIdAndAuthorId(
                PUBLISHED_EXAM_ID_ALPHA, AUTHOR_ID_ALPHA);
        assertThat(metaData).isPresent();
    }

    @Test
    void shouldReturnOrderedActiveRecords_whenFindingPublishedExams_OrderedFromLatestToOldest() {
        var metaDataPage = publishedExamMetaDataRepository.findByIsActiveOrderByPublishedDateTimeDesc(
                true, PAGE_REQUEST);

        var activePublishedExams = metaDataPage.getContent()
                .stream()
                .map(PublishedExamMetaData::getPublishedExamId)
                .collect(Collectors.toList());

        assertThat(activePublishedExams).doesNotContain(INACTIVE_PUBLISHED_EXAM_ID);
        assertThat(activePublishedExams).containsSequence(
                PUBLISHED_EXAM_ID_CHARLIE, PUBLISHED_EXAM_ID_DELTA, PUBLISHED_EXAM_ID_BETA, PUBLISHED_EXAM_ID_ALPHA);
    }

    @Test
    void shouldReturnOrderedActiveRecords_whenFindingPublishedExams_OrderedFromLatestToOldest_AfterPublishedDateTime() {
        var metaDataPage = publishedExamMetaDataRepository.findByIsActiveAndPublishedDateTimeBeforeOrderByPublishedDateTimeDesc(
                true, PAGE_REQUEST, PUBLISHED_DATE_TIME.get(PUBLISHED_EXAM_ID_CHARLIE));

        var activePublishedExams = metaDataPage.getContent()
                .stream()
                .map(PublishedExamMetaData::getPublishedExamId)
                .collect(Collectors.toList());

        assertThat(activePublishedExams).doesNotContain(INACTIVE_PUBLISHED_EXAM_ID);
        assertThat(activePublishedExams).containsSequence(PUBLISHED_EXAM_ID_DELTA, PUBLISHED_EXAM_ID_BETA, PUBLISHED_EXAM_ID_ALPHA);
    }

    @Test
    void shouldReturnOrderedActiveRecords_whenFindingPublishedExamsCreatedByAuthor_OrderedFromLatestToOldest() {
        var metaDataPage = publishedExamMetaDataRepository.findByAuthorIdAndIsActiveOrderByPublishedDateTimeDesc(
                AUTHOR_ID_ALPHA, true, PAGE_REQUEST);

        var activePublishedExams = metaDataPage.getContent()
                .stream()
                .map(PublishedExamMetaData::getPublishedExamId)
                .collect(Collectors.toList());

        assertThat(activePublishedExams).doesNotContain(INACTIVE_PUBLISHED_EXAM_ID);
        assertThat(activePublishedExams).containsSequence(PUBLISHED_EXAM_ID_CHARLIE, PUBLISHED_EXAM_ID_BETA, PUBLISHED_EXAM_ID_ALPHA);
    }

    @Test
    void shouldReturnOrderedActiveRecords_whenFindingPublishedExamsCreatedByAuthor_OrderedFromLatestToOldest_AfterPublishedDateTime() {
        var metaDataPage = publishedExamMetaDataRepository.findByAuthorIdAndIsActiveAndPublishedDateTimeBeforeOrderByPublishedDateTimeDesc(
                AUTHOR_ID_ALPHA, true, PAGE_REQUEST, PUBLISHED_DATE_TIME.get(PUBLISHED_EXAM_ID_CHARLIE));

        var activePublishedExams = metaDataPage.getContent()
                .stream()
                .map(PublishedExamMetaData::getPublishedExamId)
                .collect(Collectors.toList());

        assertThat(activePublishedExams).doesNotContain(INACTIVE_PUBLISHED_EXAM_ID);
        assertThat(activePublishedExams).containsSequence(PUBLISHED_EXAM_ID_BETA, PUBLISHED_EXAM_ID_ALPHA);
    }

    @Test
    void shouldReturnRecords_whenFindingByExamId() {
        var metaData = publishedExamMetaDataRepository.findByExamId(EXAM_ID);

        var publishedExams = metaData.stream()
                .map(PublishedExamMetaData::getPublishedExamId)
                .collect(Collectors.toList());
        assertThat(publishedExams).contains(
                PUBLISHED_EXAM_ID_ALPHA,
                PUBLISHED_EXAM_ID_BETA,
                PUBLISHED_EXAM_ID_CHARLIE,
                PUBLISHED_EXAM_ID_DELTA,
                INACTIVE_PUBLISHED_EXAM_ID);
    }
}