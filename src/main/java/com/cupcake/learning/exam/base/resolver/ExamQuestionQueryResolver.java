package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.postgres.ExamQuestion;
import com.cupcake.learning.exam.base.repository.postgres.ExamQuestionRepository;
import com.cupcake.learning.exam.base.repository.postgres.ExamRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ExamQuestionQueryResolver implements GraphQLQueryResolver {
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;

    public ExamQuestionQueryResolver(ExamRepository examRepository, ExamQuestionRepository examQuestionRepository) {
        this.examRepository = examRepository;
        this.examQuestionRepository = examQuestionRepository;
    }

    public List<ExamQuestion> examQuestions(UUID id) {
        if (!examRepository.existsByIsActiveAndId(true, id))
            throw new RuntimeException("Unable to find given exam");

        return examQuestionRepository.findByIdExamId(id);
    }
}
