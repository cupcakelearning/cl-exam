package com.cupcake.learning.exam.base.repository.postgres;

import com.cupcake.learning.exam.base.model.entity.postgres.PublishedExamMetaData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PublishedExamMetaDataRepository extends JpaRepository<PublishedExamMetaData, UUID> {
    Optional<PublishedExamMetaData> findByPublishedExamIdAndAuthorId(UUID publishedExamId, UUID authorId);

    Page<PublishedExamMetaData> findByIsActiveOrderByPublishedDateTimeDesc(boolean isActive, Pageable pageable);
    Page<PublishedExamMetaData> findByIsActiveAndPublishedDateTimeBeforeOrderByPublishedDateTimeDesc(boolean isActive, Pageable pageable, OffsetDateTime publishedDateTime);

    List<PublishedExamMetaData> findByExamId(UUID examId);
}
