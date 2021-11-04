package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.postgres.Exam;
import com.cupcake.learning.exam.base.repository.postgres.ExamRepository;
import com.cupcake.learning.exam.util.CursorUtil;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExamQueryResolverTest extends GraphQLTest {

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private CursorUtil cursorUtil;

    @BeforeEach
    void setUp() {
        when(examRepository.findByIsActiveAndId(anyBoolean(), any()))
                .thenReturn(Optional.empty());
        when(examRepository.findByAuthorIdAndIsActiveOrderByIdAsc(any(), anyBoolean(), any()))
                .thenReturn(Page.empty());
        when(examRepository.findByAuthorIdAndIsActiveAndIdAfterOrderByIdAsc(any(), anyBoolean(), any(), any()))
                .thenReturn(Page.empty());
    }

    @Test
    void exam_shouldReturnExam_whenExamExists() throws IOException {
        var examId = UUID.randomUUID();

        var exam = addMockExam(examId, UUID.randomUUID());
        var expected = Optional.of(exam);
        when(examRepository.findByIsActiveAndId(true, examId))
                .thenReturn(expected);

        Map<String, Object> input = Map.of("examId", examId);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-exam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.get("$.data.exam.id")).isEqualTo(examId.toString());
    }

    @Test
    void exam_shouldReturnError_whenExamDoNotExist() throws IOException {
        var examId = UUID.randomUUID();

        var exam = addMockExam(examId, UUID.randomUUID());
        var expected = Optional.of(exam);
        when(examRepository.findByIsActiveAndId(true, examId))
                .thenReturn(expected);

        Map<String, Object> input = Map.of("examId", UUID.randomUUID());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-exam.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void exams_shouldReturnExams_whenFilteredByAuthor() throws IOException {
        var authorId = UUID.randomUUID();

        var alpha = addMockExam(UUID.randomUUID(), authorId);
        var beta = addMockExam(UUID.randomUUID(), authorId);
        var expected = new PageImpl(List.of(alpha, beta));
        when(examRepository.findByAuthorIdAndIsActiveOrderByIdAsc(eq(authorId), eq(true), any()))
                .thenReturn(expected);

        Map<String, Object> input = Map.of(
                "authorId", authorId,
                "first", 10,
                "after", "");
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-exams.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("data").get("exams").get("edges")).hasSize(2);
    }

    @Test
    void exams_shouldReturnEmptyResult_whenFilteredByAuthor_AndAuthorMismatch() throws IOException {
        var authorId = UUID.randomUUID();

        var alpha = addMockExam(UUID.randomUUID(), authorId);
        var beta = addMockExam(UUID.randomUUID(), authorId);
        var expected = new PageImpl(List.of(alpha, beta));
        when(examRepository.findByAuthorIdAndIsActiveOrderByIdAsc(eq(authorId), eq(true), any()))
                .thenReturn(expected);

        Map<String, Object> input = Map.of(
                "authorId", UUID.randomUUID(),
                "first", 10,
                "after", "");
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-exams.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("data").get("exams").get("edges")).isEmpty();
    }

    @Test
    void exams_shouldReturnExams_whenFilteredByAuthor_AfterExamId() throws IOException {
        var authorId = UUID.randomUUID();
        var examId = UUID.randomUUID();
        String cursor = cursorUtil.createCursorWith(examId).getValue();

        var alpha = addMockExam(UUID.randomUUID(), authorId);
        var expected = new PageImpl(List.of(alpha));
        when(examRepository.findByAuthorIdAndIsActiveAndIdAfterOrderByIdAsc(eq(authorId), eq(true), any(), eq(examId)))
                .thenReturn(expected);

        Map<String, Object> input = Map.of(
                "authorId", authorId,
                "first", 10,
                "after", cursor);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-exams.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("data").get("exams").get("edges")).hasSize(1);
    }

    private Exam addMockExam(UUID examId, UUID authorId) {
        var exam = new Exam();
        exam.setId(examId);
        exam.setAuthorId(authorId);
        return exam;
    }
}