package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.postgres.Exam;
import com.cupcake.learning.exam.base.model.entity.postgres.ExamQuestion;
import com.cupcake.learning.exam.base.repository.postgres.ExamQuestionRepository;
import com.cupcake.learning.exam.base.repository.postgres.ExamRepository;
import com.cupcake.learning.exam.question.model.entity.Question;
import com.cupcake.learning.exam.question.repository.QuestionRepository;
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
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExamQuestionQueryResolverTest extends GraphQLTest {

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private CursorUtil cursorUtil;

    @BeforeEach
    void setUp() {
        when(examRepository.existsByIsActiveAndId(anyBoolean(), any()))
                .thenReturn(false);
        when(examQuestionRepository.findByIdExamId(any()))
                .thenReturn(List.of());
        when(examQuestionRepository.findByIdExamIdOrderByPositionIndexAsc(any(), any()))
                .thenReturn(Page.empty());
        when(examQuestionRepository.findByIdExamIdAndPositionIndexAfterOrderByPositionIndexAsc(any(), any(), any()))
                .thenReturn(Page.empty());
        when(questionRepository.findById(any()))
                .thenReturn(Optional.empty());
        when(examQuestionRepository.findByIdQuestionIdOrderByIdExamIdAsc(any(), any()))
                .thenReturn(Page.empty());
        when(examQuestionRepository.findByIdQuestionIdAndIdExamIdAfterOrderByIdExamIdAsc(any(), any(), any()))
                .thenReturn(Page.empty());
        when(examRepository.findById(any()))
                .thenReturn(Optional.empty());
    }

    @Test
    void examQuestions_shouldReturnExamQuestions() throws IOException {
        var examId = UUID.randomUUID();
        var expected = List.of(
                addMockExamQuestion(examId, UUID.randomUUID(), 0),
                addMockExamQuestion(examId, UUID.randomUUID(), 1));

        when(examRepository.existsByIsActiveAndId(true, examId))
                .thenReturn(true);
        when(examQuestionRepository.findByIdExamId(examId))
                .thenReturn(expected);

        Map<String, Object> input = Map.of("examId", examId);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-examQuestions.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("examQuestions")).hasSize(2);
    }

    @Test
    void examQuestions_shouldReturnError_whenExamIdNotFound() throws IOException {
        var examId = UUID.randomUUID();
        var expected = List.of(
                addMockExamQuestion(examId, UUID.randomUUID(), 0),
                addMockExamQuestion(examId, UUID.randomUUID(), 1));

        when(examRepository.existsByIsActiveAndId(true, examId))
                .thenReturn(true);
        when(examQuestionRepository.findByIdExamId(examId))
                .thenReturn(expected);

        Map<String, Object> input = Map.of("examId", UUID.randomUUID());
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-examQuestions.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void examQuestionObjects_shouldReturnExamQuestions_whenFilteredByExamId() throws IOException {
        var examId = UUID.randomUUID();
        var alphaQuestion = UUID.randomUUID();
        var betaQuestion = UUID.randomUUID();
        var expected = new PageImpl(List.of(
                addMockExamQuestion(examId, alphaQuestion, 0),
                addMockExamQuestion(examId, betaQuestion, 1)));

        when(examRepository.existsByIsActiveAndId(true, examId))
                .thenReturn(true);
        when(examQuestionRepository.findByIdExamIdOrderByPositionIndexAsc(eq(examId), any()))
                .thenReturn(expected);
        when(questionRepository.findById(alphaQuestion))
                .thenReturn(Optional.of(new Question()));
        when(questionRepository.findById(betaQuestion))
                .thenReturn(Optional.of(new Question()));

        Map<String, Object> input = Map.of(
                "examId", examId,
                "first", 10,
                "after", "");
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-examQuestionObjects.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("examQuestionObjects").get("edges")).hasSize(2);
    }

    @Test
    void examQuestionObjects_shouldReturnError_whenFilteredByExamId_AndExamIdMismatch() throws IOException {
        var examId = UUID.randomUUID();
        var alphaQuestion = UUID.randomUUID();
        var betaQuestion = UUID.randomUUID();
        var expected = new PageImpl(List.of(
                addMockExamQuestion(examId, alphaQuestion, 0),
                addMockExamQuestion(examId, betaQuestion, 1)));

        when(examRepository.existsByIsActiveAndId(true, examId))
                .thenReturn(true);
        when(examQuestionRepository.findByIdExamIdOrderByPositionIndexAsc(eq(examId), any()))
                .thenReturn(expected);
        when(questionRepository.findById(alphaQuestion))
                .thenReturn(Optional.of(new Question()));
        when(questionRepository.findById(betaQuestion))
                .thenReturn(Optional.of(new Question()));

        Map<String, Object> input = Map.of(
                "examId", UUID.randomUUID(),
                "first", 10,
                "after", "");
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-examQuestionObjects.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNotNull();
    }

    @Test
    void examQuestionObjects_shouldReturnRemainingExamQuestions_whenFilteredByExamId_AndSomeQuestionIdNotFound() throws IOException {
        var examId = UUID.randomUUID();
        var alphaQuestion = UUID.randomUUID();
        var betaQuestion = UUID.randomUUID();
        var expected = new PageImpl(List.of(
                addMockExamQuestion(examId, alphaQuestion, 0),
                addMockExamQuestion(examId, betaQuestion, 1)));

        when(examRepository.existsByIsActiveAndId(true, examId))
                .thenReturn(true);
        when(examQuestionRepository.findByIdExamIdOrderByPositionIndexAsc(eq(examId), any()))
                .thenReturn(expected);
        when(questionRepository.findById(alphaQuestion))
                .thenReturn(Optional.of(new Question()));

        Map<String, Object> input = Map.of(
                "examId", examId,
                "first", 10,
                "after", "");
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-examQuestionObjects.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("examQuestionObjects").get("edges")).hasSize(1);
    }

    @Test
    void examQuestionObjects_shouldReturnExamQuestions_whenFilteredByExamId_AfterPositionIndex() throws IOException {
        var examId = UUID.randomUUID();
        var betaQuestion = UUID.randomUUID();
        var expected = new PageImpl(List.of(
                addMockExamQuestion(examId, betaQuestion, 1)));
        String cursor = cursorUtil.createCursorWith(0).getValue();

        when(examRepository.existsByIsActiveAndId(true, examId))
                .thenReturn(true);
        when(examQuestionRepository.findByIdExamIdAndPositionIndexAfterOrderByPositionIndexAsc(eq(examId), any(), eq(0)))
                .thenReturn(expected);
        when(questionRepository.findById(betaQuestion))
                .thenReturn(Optional.of(new Question()));

        Map<String, Object> input = Map.of(
                "examId", examId,
                "first", 10,
                "after", cursor);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-examQuestionObjects.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("examQuestionObjects").get("edges")).hasSize(1);
    }

    @Test
    void examsContainingQuestion_shouldReturnExams_whenFilteredByQuestionId() throws IOException {
        var alphaExam = UUID.randomUUID();
        var betaExam = UUID.randomUUID();
        var sharedQuestion = UUID.randomUUID();
        var expected = new PageImpl(List.of(
                addMockExamQuestion(alphaExam, sharedQuestion, 0),
                addMockExamQuestion(betaExam, sharedQuestion, 1)));

        var alphaExamFound = new Exam();
        alphaExamFound.setId(alphaExam);
        var betaExamFound = new Exam();
        betaExamFound.setId(betaExam);
        
        when(examQuestionRepository.findByIdQuestionIdOrderByIdExamIdAsc(eq(sharedQuestion), any()))
                .thenReturn(expected);
        when(examRepository.findById(alphaExam))
                .thenReturn(Optional.of(alphaExamFound));
        when(examRepository.findById(betaExam))
                .thenReturn(Optional.of(betaExamFound));

        Map<String, Object> input = Map.of(
                "questionId", sharedQuestion,
                "first", 10,
                "after", "");
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-examsContainingQuestion.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("examsContainingQuestion").get("edges")).hasSize(2);
    }

    @Test
    void examsContainingQuestion_shouldReturnEmptyPage_whenFilteredByQuestionId_AndNoExamsMatched() throws IOException {
        var alphaExam = UUID.randomUUID();
        var betaExam = UUID.randomUUID();
        var sharedQuestion = UUID.randomUUID();
        var expected = new PageImpl(List.of(
                addMockExamQuestion(alphaExam, sharedQuestion, 0),
                addMockExamQuestion(betaExam, sharedQuestion, 1)));

        var alphaExamFound = new Exam();
        alphaExamFound.setId(alphaExam);
        var betaExamFound = new Exam();
        betaExamFound.setId(betaExam);

        when(examQuestionRepository.findByIdQuestionIdOrderByIdExamIdAsc(eq(sharedQuestion), any()))
                .thenReturn(expected);
        when(examRepository.findById(alphaExam))
                .thenReturn(Optional.of(alphaExamFound));
        when(examRepository.findById(betaExam))
                .thenReturn(Optional.of(betaExamFound));

        Map<String, Object> input = Map.of(
                "questionId", UUID.randomUUID(),
                "first", 10,
                "after", "");
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-examsContainingQuestion.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("examsContainingQuestion").get("edges")).hasSize(0);
    }

    @Test
    void examsContainingQuestion_shouldReturnRemainingExamQuestions_whenFilteredByExamId_AfterExamId() throws IOException {
        var alphaExam = UUID.randomUUID();
        var betaExam = UUID.randomUUID();
        var sharedQuestion = UUID.randomUUID();
        var expected = new PageImpl(List.of(
                addMockExamQuestion(betaExam, sharedQuestion, 1)));

        var betaExamFound = new Exam();
        betaExamFound.setId(betaExam);

        String cursor = cursorUtil.createCursorWith(alphaExam).getValue();

        when(examQuestionRepository.findByIdQuestionIdAndIdExamIdAfterOrderByIdExamIdAsc(eq(sharedQuestion), any(), eq(alphaExam)))
                .thenReturn(expected);
        when(examRepository.findById(betaExam))
                .thenReturn(Optional.of(betaExamFound));

        Map<String, Object> input = Map.of(
                "questionId", sharedQuestion,
                "first", 10,
                "after", cursor);
        GraphQLResponse response = graphQLTestTemplate.perform(
                "query-examsContainingQuestion.graphql",
                toVariablesInput(input));

        assertThat(response.readTree().get("errors")).isNull();
        assertThat(response.readTree().get("data").get("examsContainingQuestion").get("edges")).hasSize(1);
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