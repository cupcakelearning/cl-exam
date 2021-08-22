package com.cupcake.learning.exam.question.repository;

import com.cupcake.learning.exam.question.model.entity.Question;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionRepository extends CrudRepository<Question, UUID> {
    @EnableScan
    boolean existsByIdAndAuthorId(UUID id, UUID authorId);
}
