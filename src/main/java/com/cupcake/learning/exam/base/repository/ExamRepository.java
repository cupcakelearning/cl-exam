package com.cupcake.learning.exam.base.repository;

import com.cupcake.learning.exam.base.model.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {
    Optional<Exam> findByIdAndAuthorId(UUID id, UUID authorId);

    Page<Exam> findByOrderByIdAsc(Pageable pageable);
    Page<Exam> findByIdAfterOrderByIdAsc(Pageable pageable, UUID id);
}
