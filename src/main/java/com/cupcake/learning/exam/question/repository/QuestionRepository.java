package com.cupcake.learning.exam.question.repository;

import com.cupcake.learning.exam.question.model.entity.Question;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionRepository extends CrudRepository<Question, UUID> {
    @EnableScan
    @EnableScanCount
    boolean existsByIdAndAuthorId(UUID id, UUID authorId);
}
