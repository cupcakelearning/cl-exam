package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.dynamo.PublishedExam;
import com.cupcake.learning.exam.base.model.entity.postgres.Exam;
import com.cupcake.learning.exam.base.model.entity.postgres.PublishedExamMetaData;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PublishedExamMutationResolverTest extends GraphQLTest {

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @BeforeEach
    void setUp() {
        when(examRepository.findByIsActiveAndIdAndAuthorId(anyBoolean(), any(), any()))
                .thenReturn(Optional.empty());
        when(examQuestionRepository.findByIdExamId(any()))
                .thenReturn(List.of());
        when(questionRepository.findAllById(any()))
                .thenReturn(List.of());
        when(publishedExamMetaDataRepository.findByPublishedExamIdAndAuthorId(any(), any()))
                .thenReturn(Optional.empty());
    }

    @Test
    void publishExam_shouldReturnPublishedExam() throws IOException {
        var examId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var exam = new Exam();
        exam.setId(examId);
        exam.setPrice(BigDecimal.ONE);

        when(examRepository.findByIsActiveAndIdAndAuthorId(true, examId, authorId))
                .thenReturn(Optional.of(exam));
        when(publishedExamRepository.save(any())).thenReturn(new PublishedExam());
        when(publishedExamMetaDataRepository.save(any())).thenReturn(new PublishedExamMetaData());

        Map<String, Object> input = Map.of(
                "examId", examId,
                "authorId", authorId);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-publishExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("publishExam")).isNotNull();
    }

    @Test
    void publishExam_shouldReturnError_whenExamPriceIsNotSet() throws IOException {
        var examId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var exam = new Exam();
        exam.setId(examId);

        when(examRepository.findByIsActiveAndIdAndAuthorId(true, examId, authorId))
                .thenReturn(Optional.of(exam));
        when(publishedExamRepository.save(any())).thenReturn(new PublishedExam());
        when(publishedExamMetaDataRepository.save(any())).thenReturn(new PublishedExamMetaData());

        Map<String, Object> input = Map.of(
                "examId", examId,
                "authorId", authorId);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-publishExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void publishExam_shouldReturnError_whenExamPriceIsZero() throws IOException {
        var examId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var exam = new Exam();
        exam.setId(examId);
        exam.setPrice(BigDecimal.ZERO);

        when(examRepository.findByIsActiveAndIdAndAuthorId(true, examId, authorId))
                .thenReturn(Optional.of(exam));
        when(publishedExamRepository.save(any())).thenReturn(new PublishedExam());
        when(publishedExamMetaDataRepository.save(any())).thenReturn(new PublishedExamMetaData());

        Map<String, Object> input = Map.of(
                "examId", examId,
                "authorId", authorId);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-publishExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void publishExam_shouldReturnError_whenExamPriceIsLessThanZero() throws IOException {
        var examId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var exam = new Exam();
        exam.setId(examId);
        exam.setPrice(BigDecimal.valueOf(-1));

        when(examRepository.findByIsActiveAndIdAndAuthorId(true, examId, authorId))
                .thenReturn(Optional.of(exam));
        when(publishedExamRepository.save(any())).thenReturn(new PublishedExam());
        when(publishedExamMetaDataRepository.save(any())).thenReturn(new PublishedExamMetaData());

        Map<String, Object> input = Map.of(
                "examId", examId,
                "authorId", authorId);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-publishExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void unpublishExam_shouldReturnUUID() throws IOException {
        var publishedExamId = UUID.randomUUID();
        var authorId = UUID.randomUUID();

        when(publishedExamMetaDataRepository.findByPublishedExamIdAndAuthorId(publishedExamId, authorId))
                .thenReturn(Optional.of(new PublishedExamMetaData()));
        when(publishedExamMetaDataRepository.save(any())).thenReturn(new PublishedExamMetaData());

        Map<String, Object> input = Map.of(
                "publishedExamId", publishedExamId,
                "authorId", authorId);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-unpublishExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.get("$.data.unpublishExam")).isEqualTo(publishedExamId.toString());
    }

    @Test
    void unpublishExam_shouldReturnError_whenPublishedExamIdDoNotExist() throws IOException {
        var publishedExamId = UUID.randomUUID();
        var authorId = UUID.randomUUID();

        when(publishedExamMetaDataRepository.findByPublishedExamIdAndAuthorId(publishedExamId, authorId))
                .thenReturn(Optional.of(new PublishedExamMetaData()));
        when(publishedExamMetaDataRepository.save(any())).thenReturn(new PublishedExamMetaData());

        Map<String, Object> input = Map.of(
                "publishedExamId", UUID.randomUUID(),
                "authorId", authorId);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-unpublishExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void republishExam_shouldReturnUUID() throws IOException {
        var publishedExamId = UUID.randomUUID();
        var authorId = UUID.randomUUID();

        when(publishedExamMetaDataRepository.findByPublishedExamIdAndAuthorId(publishedExamId, authorId))
                .thenReturn(Optional.of(new PublishedExamMetaData()));
        when(publishedExamMetaDataRepository.save(any())).thenReturn(new PublishedExamMetaData());

        Map<String, Object> input = Map.of(
                "publishedExamId", publishedExamId,
                "authorId", authorId);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-republishExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("republishExam")).isNotNull();
    }

    @Test
    void republishExam_shouldReturnError_whenPublishedExamIdDoNotExist() throws IOException {
        var publishedExamId = UUID.randomUUID();
        var authorId = UUID.randomUUID();

        when(publishedExamMetaDataRepository.findByPublishedExamIdAndAuthorId(publishedExamId, authorId))
                .thenReturn(Optional.of(new PublishedExamMetaData()));
        when(publishedExamMetaDataRepository.save(any())).thenReturn(new PublishedExamMetaData());

        Map<String, Object> input = Map.of(
                "publishedExamId", UUID.randomUUID(),
                "authorId", authorId);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-republishExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }
}