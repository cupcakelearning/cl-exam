package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.postgres.Exam;
import com.cupcake.learning.exam.base.model.input.ExamInput;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExamMutationResolverTest extends GraphQLTest {
    private static final UUID AUTHOR_ID = UUID.randomUUID();

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @BeforeEach
    void setUp() {
        when(userRepository.existsById(AUTHOR_ID)).thenReturn(true);
    }

    @Test
    void addExam_shouldReturnExam() throws IOException {
        when(examRepository.save(any())).thenReturn(new Exam());

        Map<String, Object> input = Map.of(
                "authorId", AUTHOR_ID,
                "examInput", new ExamInput());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-addExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("addExam")).isNotNull();
    }

    @Test
    void addExam_shouldReturnError_whenUserDoesNotExists() throws IOException {
        when(examRepository.save(any())).thenReturn(new Exam());

        Map<String, Object> input = Map.of(
                "authorId", UUID.randomUUID(),
                "examInput", new ExamInput());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-addExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void updateExam_shouldReturnExam() throws IOException {
        var examId = UUID.randomUUID();

        when(examRepository.findByIsActiveAndIdAndAuthorId(eq(true), eq(examId), eq(AUTHOR_ID))).thenReturn(Optional.of(new Exam()));
        when(examRepository.save(any())).thenReturn(new Exam());

        Map<String, Object> input = Map.of(
                "examId", examId,
                "authorId", AUTHOR_ID,
                "examInput", new ExamInput());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-updateExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("updateExam")).isNotNull();
    }

    @Test
    void updateExam_shouldReturnError_whenAuthorMismatch() throws IOException {
        var examId = UUID.randomUUID();

        when(examRepository.findByIsActiveAndIdAndAuthorId(eq(true), eq(examId), eq(AUTHOR_ID))).thenReturn(Optional.of(new Exam()));
        when(examRepository.save(any())).thenReturn(new Exam());

        Map<String, Object> input = Map.of(
                "examId", examId,
                "authorId", UUID.randomUUID(),
                "examInput", new ExamInput());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-updateExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void updateExam_shouldReturnError_whenExamIdMismatch() throws IOException {
        var examId = UUID.randomUUID();

        when(examRepository.findByIsActiveAndIdAndAuthorId(eq(true), eq(examId), eq(AUTHOR_ID))).thenReturn(Optional.of(new Exam()));
        when(examRepository.save(any())).thenReturn(new Exam());

        Map<String, Object> input = Map.of(
                "examId", UUID.randomUUID(),
                "authorId", AUTHOR_ID,
                "examInput", new ExamInput());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-updateExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void updateExam_shouldReturnError_whenExamIsFrozen() throws IOException {
        var examId = UUID.randomUUID();

        when(examRepository.findByIsActiveAndIdAndAuthorId(eq(true), eq(examId), eq(AUTHOR_ID))).thenReturn(Optional.empty());
        when(examRepository.save(any())).thenReturn(new Exam());

        Map<String, Object> input = Map.of(
                "examId", examId,
                "authorId", AUTHOR_ID,
                "examInput", new ExamInput());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-updateExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void freezeExam_shouldReturnUUID() throws IOException {
        var examId = UUID.randomUUID();

        when(examRepository.findByIsActiveAndIdAndAuthorId(eq(true), eq(examId), eq(AUTHOR_ID))).thenReturn(Optional.of(new Exam()));
        when(examRepository.save(any())).thenReturn(new Exam());

        Map<String, Object> input = Map.of(
                "examId", examId,
                "authorId", AUTHOR_ID,
                "examInput", new ExamInput());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-freezeExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.get("$.data.freezeExam")).isEqualTo(examId.toString());
    }

    @Test
    void freezeExam_shouldReturnError_whenAuthorMismatch() throws IOException {
        var examId = UUID.randomUUID();

        when(examRepository.findByIsActiveAndIdAndAuthorId(eq(true), eq(examId), eq(AUTHOR_ID))).thenReturn(Optional.of(new Exam()));
        when(examRepository.save(any())).thenReturn(new Exam());

        Map<String, Object> input = Map.of(
                "examId", examId,
                "authorId", UUID.randomUUID(),
                "examInput", new ExamInput());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-freezeExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void freezeExam_shouldReturnError_whenExamIdMismatch() throws IOException {
        var examId = UUID.randomUUID();

        when(examRepository.findByIsActiveAndIdAndAuthorId(eq(true), eq(examId), eq(AUTHOR_ID))).thenReturn(Optional.of(new Exam()));
        when(examRepository.save(any())).thenReturn(new Exam());

        Map<String, Object> input = Map.of(
                "examId", UUID.randomUUID(),
                "authorId", AUTHOR_ID,
                "examInput", new ExamInput());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-freezeExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void freezeExam_shouldReturnError_whenExamIsFrozen() throws IOException {
        var examId = UUID.randomUUID();

        when(examRepository.findByIsActiveAndIdAndAuthorId(eq(true), eq(examId), eq(AUTHOR_ID))).thenReturn(Optional.empty());
        when(examRepository.save(any())).thenReturn(new Exam());

        Map<String, Object> input = Map.of(
                "examId", examId,
                "authorId", AUTHOR_ID,
                "examInput", new ExamInput());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-freezeExam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }
}