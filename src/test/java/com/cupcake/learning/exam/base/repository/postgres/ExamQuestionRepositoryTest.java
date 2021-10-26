package com.cupcake.learning.exam.base.repository.postgres;

import com.cupcake.learning.exam.base.model.entity.postgres.ExamQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaRepositories
class ExamQuestionRepositoryTest {
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 10);
    private static final UUID EXAM_ALPHA            = UUID.fromString("11ddabe7-3208-4c1b-82c8-7816ef6c38b3");
    private static final UUID EXAM_BETA             = UUID.fromString("21ddabe7-3208-4c1b-82c8-7816ef6c38b3");
    private static final UUID EXAM_CHARLIE          = UUID.fromString("31ddabe7-3208-4c1b-82c8-7816ef6c38b3");
    private static final UUID ALPHA_FIRST_QUESTION  = UUID.fromString("1eb131da-2e50-42a6-a460-8d2266ff797c");
    private static final UUID ALPHA_SECOND_QUESTION = UUID.fromString("2eb131da-2e50-42a6-a460-8d2266ff797c");
    private static final UUID ALPHA_FOURTH_QUESTION = UUID.fromString("3eb131da-2e50-42a6-a460-8d2266ff797c");
    private static final UUID SHARED_QUESTION       = UUID.fromString("beb131da-2e50-42a6-a460-8d2266ff797c");

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @BeforeEach
    void setUp() {
        addRecords(EXAM_ALPHA, ALPHA_FIRST_QUESTION, 0);
        addRecords(EXAM_ALPHA, ALPHA_SECOND_QUESTION, 1);
        addRecords(EXAM_ALPHA, SHARED_QUESTION, 2);
        addRecords(EXAM_ALPHA, ALPHA_FOURTH_QUESTION, 3);
        addRecords(EXAM_BETA, SHARED_QUESTION, 0);
        addRecords(EXAM_CHARLIE, SHARED_QUESTION, 0);
    }

    private void addRecords(UUID examId, UUID questionId, int position) {
        var examQuestionId = new ExamQuestion.ExamQuestionId();
        examQuestionId.setExamId(examId);
        examQuestionId.setQuestionId(questionId);

        var examQuestion = new ExamQuestion();
        examQuestion.setId(examQuestionId);
        examQuestion.setPositionIndex(position);
        examQuestionRepository.save(examQuestion);
    }

    @Test
    void shouldReturnRecords_whenFindingByExamId() {
        var alpha = examQuestionRepository.findByIdExamId(EXAM_ALPHA);
        var beta = examQuestionRepository.findByIdExamId(EXAM_BETA);

        assertThat(alpha).hasSize(4);
        assertThat(beta).hasSize(1);
    }

    @Test
    void shouldReturnOrderedResult_whenFindingByExamId_OrderedByPositionIndex() {
        var alphaPage = examQuestionRepository.findByIdExamIdOrderByPositionIndexAsc(EXAM_ALPHA, PAGE_REQUEST);
        var alphaQuestions = alphaPage.getContent()
                .stream()
                .map(ExamQuestion::getId)
                .map(ExamQuestion.ExamQuestionId::getQuestionId)
                .collect(Collectors.toList());

        assertThat(alphaQuestions)
                .containsSequence(ALPHA_FIRST_QUESTION, ALPHA_SECOND_QUESTION, SHARED_QUESTION, ALPHA_FOURTH_QUESTION);
    }

    @Test
    void shouldReturnOrderedResult_whenFindingByExamId_OrderedByPositionIndex_AfterPositionIndex() {
        var alphaPage = examQuestionRepository.findByIdExamIdAndPositionIndexAfterOrderByPositionIndexAsc(
                EXAM_ALPHA, PAGE_REQUEST, 1);
        var alphaQuestions = alphaPage.getContent()
                .stream()
                .map(ExamQuestion::getId)
                .map(ExamQuestion.ExamQuestionId::getQuestionId)
                .collect(Collectors.toList());

        assertThat(alphaQuestions)
                .containsSequence(SHARED_QUESTION, ALPHA_FOURTH_QUESTION);
    }

    @Test
    void shouldReturnOrderedResult_whenFindingByQuestionId_OrderedByExamIndex() {
        var sharedPage = examQuestionRepository.findByIdQuestionIdOrderByIdExamIdAsc(SHARED_QUESTION, PAGE_REQUEST);
        var sharedExams = sharedPage.getContent()
                .stream()
                .map(ExamQuestion::getId)
                .map(ExamQuestion.ExamQuestionId::getExamId)
                .collect(Collectors.toList());

        assertThat(sharedExams)
                .containsSequence(EXAM_ALPHA, EXAM_BETA, EXAM_CHARLIE);
    }

    @Test
    void shouldReturnOrderedResult_whenFindingByQuestionId_OrderedByExamIndex_AfterExamId() {
        var sharedPage = examQuestionRepository.findByIdQuestionIdAndIdExamIdAfterOrderByIdExamIdAsc(
                SHARED_QUESTION, PAGE_REQUEST, EXAM_BETA);
        var sharedExams = sharedPage.getContent()
                .stream()
                .map(ExamQuestion::getId)
                .map(ExamQuestion.ExamQuestionId::getExamId)
                .collect(Collectors.toList());

        assertThat(sharedExams)
                .containsSequence(EXAM_CHARLIE);
    }
}