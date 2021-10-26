package com.cupcake.learning.exam.base.repository.postgres;

import com.cupcake.learning.exam.base.model.entity.postgres.Exam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaRepositories
class ExamRepositoryTest {
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 10);
    private static final UUID AUTHOR_ID = UUID.randomUUID();

    private UUID examIdAlpha;
    private UUID examIdBeta;
    private UUID examIdCharlie;
    private UUID inactiveExamId;
    
    @Autowired
    private ExamRepository examRepository;

    @BeforeEach
    void setUp() {
        inactiveExamId = addExam(false);

        var examIds = new TreeSet<String>();
        for (int i = 0; i < 3; i++) {
            examIds.add(addExam(true).toString());
        }

        Iterator<String> iterator = examIds.iterator();
        examIdAlpha = UUID.fromString(iterator.next());
        examIdBeta = UUID.fromString(iterator.next());
        examIdCharlie = UUID.fromString(iterator.next());
    }

    private UUID addExam(boolean active) {
        var exam = new Exam();
        exam.setAuthorId(AUTHOR_ID);
        exam.setActive(active);

        return examRepository.save(exam).getId();
    }

    @Test
    void shouldReturnActiveExam_whenFindingByExamIdAndAuthor() {
        var exam = examRepository.findByIsActiveAndIdAndAuthorId(true, examIdAlpha, AUTHOR_ID);
        assertThat(exam).isPresent();
    }

    @Test
    void shouldReturnNothing_whenFindingByExamIdAndAuthor_AndExamIsInactive() {
        var exam = examRepository.findByIsActiveAndIdAndAuthorId(true, inactiveExamId, AUTHOR_ID);
        assertThat(exam).isNotPresent();
    }

    @Test
    void shouldReturnTrue_whenActiveExamExist() {
        var exam = examRepository.existsByIsActiveAndId(true, examIdAlpha);
        assertThat(exam).isTrue();
    }

    @Test
    void shouldReturnNothing_whenExamExist_AndIsInactive() {
        var exam = examRepository.existsByIsActiveAndId(true, inactiveExamId);
        assertThat(exam).isFalse();
    }

    @Test
    void shouldReturnTrue_whenActiveExamByAuthorExist() {
        var exam = examRepository.existsByIsActiveAndIdAndAuthorId(true, examIdAlpha, AUTHOR_ID);
        assertThat(exam).isTrue();
    }

    @Test
    void shouldReturnNothing_whenExamByAuthorExist_AndIsInactive() {
        var exam = examRepository.existsByIsActiveAndIdAndAuthorId(true, inactiveExamId, AUTHOR_ID);
        assertThat(exam).isFalse();
    }

    @Test
    void shouldReturnActiveExam_whenFindingByExamId() {
        var exam = examRepository.findByIsActiveAndId(true, examIdAlpha);
        assertThat(exam).isPresent();
    }

    @Test
    void shouldReturnNothing_whenFindingByExamId_AndExamIsInactive() {
        var exam = examRepository.findByIsActiveAndId(true, inactiveExamId);
        assertThat(exam).isNotPresent();
    }

    @Test
    void shouldReturnOrderedActiveExams_whenFindingExamsCreatedByAuthor_OrderedByExamId() {
        var examPage = examRepository.findByAuthorIdAndIsActiveOrderByIdAsc(AUTHOR_ID, true, PAGE_REQUEST);

        var activeExams = examPage.getContent()
                .stream()
                .map(Exam::getId)
                .collect(Collectors.toList());

        assertThat(activeExams).doesNotContain(inactiveExamId);
        assertThat(activeExams).containsSequence(examIdAlpha, examIdBeta, examIdCharlie);
    }

    @Test
    void shouldReturnOrderedActiveExams_whenFindingExamsCreatedByAuthor_OrderedByExamId_AfterExamId() {
        var examPage = examRepository.findByAuthorIdAndIsActiveAndIdAfterOrderByIdAsc(
                AUTHOR_ID,
                true,
                PAGE_REQUEST,
                examIdBeta);

        var activeExams = examPage.getContent()
                .stream()
                .map(Exam::getId)
                .collect(Collectors.toList());

        assertThat(activeExams).doesNotContain(inactiveExamId);
        assertThat(activeExams).containsSequence(examIdCharlie);
    }
}