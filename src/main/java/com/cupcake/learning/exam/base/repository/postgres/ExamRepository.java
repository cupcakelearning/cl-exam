package com.cupcake.learning.exam.base.repository.postgres;

import com.cupcake.learning.exam.base.model.entity.postgres.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {
    Optional<Exam> findByIsActiveAndIdAndAuthorId(boolean isActive, UUID id, UUID authorId);
    boolean existsByIsActiveAndId(boolean isActive, UUID id);
    boolean existsByIsActiveAndIdAndAuthorId(boolean isActive, UUID id, UUID authorId);

    Optional<Exam> findByIsActiveAndId(boolean isActive, UUID id);
    Page<Exam> findByAuthorIdAndIsActiveOrderByIdAsc(UUID authorId, boolean isActive, Pageable pageable);
    Page<Exam> findByAuthorIdAndIsActiveAndIdAfterOrderByIdAsc(UUID authorId, boolean isActive, Pageable pageable, UUID id);
}
