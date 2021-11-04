package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.postgres.ExamQuestion;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExamQuestionMutationResolverTest extends GraphQLTest {
    private static final UUID AUTHOR_ID = UUID.randomUUID();
    private static final UUID EXAM_ID = UUID.randomUUID();
    private static final List<UUID> QUESTION_IDS = List.of(UUID.randomUUID(), UUID.randomUUID());

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @BeforeEach
    void setUp() {
        when(examRepository.existsByIsActiveAndIdAndAuthorId(anyBoolean(), any(), any()))
                .thenReturn(false);
        when(questionRepository.existsByIdAndAuthorId(any(), any()))
                .thenReturn(false);
        when(examQuestionRepository.findByIdExamId(any()))
                .thenReturn(List.of());

        setupBaseSuccessCase();
    }

    private void setupBaseSuccessCase() {
        var expected = new ArrayList<ExamQuestion>();
        for (int i = 0; i < QUESTION_IDS.size(); i++) {
            expected.add(addMockExamQuestion(EXAM_ID, QUESTION_IDS.get(i), i));
        }

        when(examRepository.existsByIsActiveAndIdAndAuthorId(true, EXAM_ID, AUTHOR_ID))
                .thenReturn(true);
        for (var questionId : QUESTION_IDS) {
            when(questionRepository.existsByIdAndAuthorId(questionId, AUTHOR_ID))
                    .thenReturn(true);
        }
        when(examQuestionRepository.saveAll(anyList()))
                .thenReturn(expected);
    }

    @Test
    void setExamQuestions_shouldReturnExamQuestions() throws IOException {
        Map<String, Object> input = Map.of(
                "examId", EXAM_ID,
                "authorId", AUTHOR_ID,
                "questionIds", QUESTION_IDS);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-setExamQuestions.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("setExamQuestions")).hasSize(QUESTION_IDS.size());
    }

    @Test
    void setExamQuestions_shouldReturnExamQuestions_whenExamIdDoNotExist() throws IOException {
        Map<String, Object> input = Map.of(
                "examId", UUID.randomUUID(),
                "authorId", AUTHOR_ID,
                "questionIds", QUESTION_IDS);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-setExamQuestions.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void setExamQuestions_shouldReturnExamQuestions_whenAuthorMisMatch() throws IOException {
        Map<String, Object> input = Map.of(
                "examId", EXAM_ID,
                "authorId", UUID.randomUUID(),
                "questionIds", QUESTION_IDS);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-setExamQuestions.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void setExamQuestions_shouldReturnError_whenQuestionDoNotExist() throws IOException {
        var questionIds = new ArrayList<>(QUESTION_IDS);
        questionIds.set(0, UUID.randomUUID());

        Map<String, Object> input = Map.of(
                "examId", EXAM_ID,
                "authorId", AUTHOR_ID,
                "questionIds", questionIds);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "mutation-setExamQuestions.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    private ExamQuestion addMockExamQuestion(UUID examId, UUID questionId, int position) {
        var examQuestionId = new ExamQuestion.ExamQuestionId();
        examQuestionId.setExamId(examId);
        examQuestionId.setQuestionId(questionId);

        var examQuestion = new ExamQuestion();
        examQuestion.setId(examQuestionId);
        examQuestion.setPositionIndex(position);
        return examQuestion;
    }
}