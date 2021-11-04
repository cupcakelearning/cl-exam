package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.dynamo.PublishedExam;
import com.cupcake.learning.exam.base.model.entity.postgres.PublishedExamMetaData;
import com.cupcake.learning.exam.util.CursorUtil;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PublishedExamQueryResolverTest extends GraphQLTest {
    private final UUID AUTHOR_ID = UUID.randomUUID();

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private CursorUtil cursorUtil;

    @BeforeEach
    void setUp() {
        when(publishedExamMetaDataRepository.findByAuthorIdAndIsActiveOrderByPublishedDateTimeDesc(any(), anyBoolean(), any()))
                .thenReturn(Page.empty());
        when(publishedExamMetaDataRepository.findByIsActiveOrderByPublishedDateTimeDesc(anyBoolean(), any()))
                .thenReturn(Page.empty());
        when(publishedExamMetaDataRepository.findByAuthorIdAndIsActiveAndPublishedDateTimeBeforeOrderByPublishedDateTimeDesc(any(), anyBoolean(), any(), any()))
                .thenReturn(Page.empty());
        when(publishedExamMetaDataRepository.findByIsActiveAndPublishedDateTimeBeforeOrderByPublishedDateTimeDesc(anyBoolean(), any(), any()))
                .thenReturn(Page.empty());
        when(publishedExamMetaDataRepository.findByExamId(any()))
                .thenReturn(List.of());
        when(publishedExamMetaDataRepository.findByPublishedExamId(any()))
                .thenReturn(Optional.empty());
        when(publishedExamMetaDataRepository.findById(any()))
                .thenReturn(Optional.empty());
    }

    @Test
    void publishedExamsForSale_shouldReturnActivePublishedExams() throws IOException {
        var publishedDateTime = OffsetDateTime.now();
        var expected = new PageImpl(List.of(
                addMockPublishedExamMetaData(publishedDateTime),
                addMockPublishedExamMetaData(publishedDateTime)));

        when(publishedExamMetaDataRepository.findByIsActiveOrderByPublishedDateTimeDesc(eq(true), any()))
                .thenReturn(expected);

        Map<String, Object> input = Map.of(
                "first", 10,
                "after", "");
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-publishedExamsForSale.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("publishedExamsForSale").get("edges")).hasSize(2);
    }

    @Test
    void publishedExamsForSale_shouldReturnActivePublishedExams_whenFilteredByAuthor() throws IOException {
        var publishedDateTime = OffsetDateTime.now();
        var expected = new PageImpl(List.of(
                addMockPublishedExamMetaData(publishedDateTime),
                addMockPublishedExamMetaData(publishedDateTime)));

        when(publishedExamMetaDataRepository.findByAuthorIdAndIsActiveOrderByPublishedDateTimeDesc(eq(AUTHOR_ID), eq(true), any()))
                .thenReturn(expected);

        Map<String, Object> input = Map.of(
                "authorId", AUTHOR_ID,
                "first", 10,
                "after", "");
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-publishedExamsForSale.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("publishedExamsForSale").get("edges")).hasSize(2);
    }

    @Test
    void publishedExamsForSale_shouldReturnEmptyPage_whenFilteredByAuthor_andAuthorMismatch() throws IOException {
        var publishedDateTime = OffsetDateTime.now();
        var expected = new PageImpl(List.of(
                addMockPublishedExamMetaData(publishedDateTime),
                addMockPublishedExamMetaData(publishedDateTime)));

        when(publishedExamMetaDataRepository.findByAuthorIdAndIsActiveOrderByPublishedDateTimeDesc(eq(AUTHOR_ID), eq(true), any()))
                .thenReturn(expected);

        Map<String, Object> input = Map.of(
                "authorId", UUID.randomUUID(),
                "first", 10,
                "after", "");
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-publishedExamsForSale.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("publishedExamsForSale").get("edges")).hasSize(0);
    }

    @Test
    void publishedExamsForSale_shouldReturnActivePublishedExams_afterPublishedDateTime() throws IOException {
        var publishedDateTime = OffsetDateTime.now();
        var expected = new PageImpl(List.of(
                addMockPublishedExamMetaData(publishedDateTime),
                addMockPublishedExamMetaData(publishedDateTime)));

        String cursor = cursorUtil.createCursorWith(publishedDateTime).getValue();

        when(publishedExamMetaDataRepository.findByIsActiveAndPublishedDateTimeBeforeOrderByPublishedDateTimeDesc(eq(true), any(), eq(publishedDateTime)))
                .thenReturn(expected);

        Map<String, Object> input = Map.of(
                "first", 10,
                "after", cursor);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-publishedExamsForSale.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("publishedExamsForSale").get("edges")).hasSize(2);
    }

    @Test
    void publishedExamsForSale_shouldReturnActivePublishedExams_whenFilteredByAuthor_afterPublishedDateTime() throws IOException {
        var publishedDateTime = OffsetDateTime.now();
        var expected = new PageImpl(List.of(
                addMockPublishedExamMetaData(publishedDateTime),
                addMockPublishedExamMetaData(publishedDateTime)));

        String cursor = cursorUtil.createCursorWith(publishedDateTime).getValue();

        when(publishedExamMetaDataRepository.findByAuthorIdAndIsActiveAndPublishedDateTimeBeforeOrderByPublishedDateTimeDesc(eq(AUTHOR_ID), eq(true), any(), eq(publishedDateTime)))
                .thenReturn(expected);

        Map<String, Object> input = Map.of(
                "authorId", AUTHOR_ID,
                "first", 10,
                "after", cursor);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-publishedExamsForSale.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("publishedExamsForSale").get("edges")).hasSize(2);
    }

    @Test
    void publishedExamsForSale_shouldReturnEmptyPage_whenFilteredByAuthor_afterPublishedDateTime_andAuthorMismatch() throws IOException {
        var publishedDateTime = OffsetDateTime.now();
        var expected = new PageImpl(List.of(
                addMockPublishedExamMetaData(publishedDateTime),
                addMockPublishedExamMetaData(publishedDateTime)));

        String cursor = cursorUtil.createCursorWith(publishedDateTime).getValue();

        when(publishedExamMetaDataRepository.findByAuthorIdAndIsActiveAndPublishedDateTimeBeforeOrderByPublishedDateTimeDesc(eq(AUTHOR_ID), eq(true), any(), eq(publishedDateTime)))
                .thenReturn(expected);

        Map<String, Object> input = Map.of(
                "authorId", UUID.randomUUID(),
                "first", 10,
                "after", cursor);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-publishedExamsForSale.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("publishedExamsForSale").get("edges")).hasSize(0);
    }

    @Test
    void getPublishedExamsForBaseExam_shouldReturnPublishedExams() throws IOException {
        var examId = UUID.randomUUID();

        when(publishedExamMetaDataRepository.findByExamId(examId))
                .thenReturn(List.of(new PublishedExamMetaData(), new PublishedExamMetaData()));

        Map<String, Object> input = Map.of(
                "examId", examId);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-getPublishedExamsForBaseExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("getPublishedExamsForBaseExam")).hasSize(2);
    }

    @Test
    void getPublishedExamsForBaseExam_shouldReturnPublishedExams_whenExamIdMismatch() throws IOException {
        var examId = UUID.randomUUID();

        when(publishedExamMetaDataRepository.findByExamId(examId))
                .thenReturn(List.of(new PublishedExamMetaData(), new PublishedExamMetaData()));

        Map<String, Object> input = Map.of(
                "examId", UUID.randomUUID());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-getPublishedExamsForBaseExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("getPublishedExamsForBaseExam")).hasSize(0);
    }
    
    @Test
    void getBaseExamForPublishedExam_shouldReturnPublishedExams() throws IOException {
        var publishedExamId = UUID.randomUUID();

        when(publishedExamMetaDataRepository.findByPublishedExamId(publishedExamId))
                .thenReturn(Optional.of(new PublishedExamMetaData()));

        Map<String, Object> input = Map.of(
                "publishedExamId", publishedExamId);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-getBaseExamForPublishedExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("getBaseExamForPublishedExam")).isNotNull();
    }

    @Test
    void getBaseExamForPublishedExam_shouldReturnPublishedExams_whenExamIdMismatch() throws IOException {
        var publishedExamId = UUID.randomUUID();

        when(publishedExamMetaDataRepository.findByPublishedExamId(publishedExamId))
                .thenReturn(Optional.of(new PublishedExamMetaData()));

        Map<String, Object> input = Map.of(
                "publishedExamId", UUID.randomUUID());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-getBaseExamForPublishedExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }
    
    @Test
    void publishedExam_shouldReturnPublishedExams() throws IOException {
        var publishedExamId = UUID.randomUUID();

        when(publishedExamRepository.findById(publishedExamId))
                .thenReturn(Optional.of(new PublishedExam()));

        Map<String, Object> input = Map.of(
                "publishedExamId", publishedExamId);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-publishedExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("publishedExam")).isNotNull();
    }

    @Test
    void publishedExam_shouldReturnPublishedExams_whenExamIdMismatch() throws IOException {
        var publishedExamId = UUID.randomUUID();

        when(publishedExamRepository.findById(publishedExamId))
                .thenReturn(Optional.of(new PublishedExam()));

        Map<String, Object> input = Map.of(
                "publishedExamId", UUID.randomUUID());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-publishedExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    private PublishedExamMetaData addMockPublishedExamMetaData(OffsetDateTime publishedDateTime) {
        var metaData = new PublishedExamMetaData();
        metaData.setPublishedDateTime(publishedDateTime);

        return metaData;
    }
}