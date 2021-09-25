package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.postgres.ExamQuestion;
import com.cupcake.learning.exam.base.repository.postgres.ExamQuestionRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ExamQuestionQueryResolver implements GraphQLQueryResolver {
    private final ExamQuestionRepository examQuestionRepository;

    public ExamQuestionQueryResolver(ExamQuestionRepository examQuestionRepository) {
        this.examQuestionRepository = examQuestionRepository;
    }

    public List<ExamQuestion> examQuestions(UUID id) {
        return examQuestionRepository.findByIdExamId(id);
    }
}
