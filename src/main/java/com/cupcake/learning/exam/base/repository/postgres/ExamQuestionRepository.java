package com.cupcake.learning.exam.base.repository.postgres;

import com.cupcake.learning.exam.base.model.entity.postgres.ExamQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, ExamQuestion.ExamQuestionId> {
    List<ExamQuestion> findByIdExamId(UUID examId);
    Page<ExamQuestion> findByIdExamIdOrderByPositionIndexAsc(UUID examId, Pageable pageable);
    Page<ExamQuestion> findByIdExamIdAndPositionIndexAfterOrderByPositionIndexAsc(UUID examId, Pageable pageable, Integer positionIndex);
    Page<ExamQuestion> findByIdQuestionIdOrderByIdExamIdAsc(UUID questionId, Pageable pageable);
    Page<ExamQuestion> findByIdQuestionIdAndIdExamIdAfterOrderByIdExamIdAsc(UUID questionId, Pageable pageable, UUID examId);
}
