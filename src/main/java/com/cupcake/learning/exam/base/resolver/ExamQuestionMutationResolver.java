package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.postgres.ExamQuestion;
import com.cupcake.learning.exam.base.repository.postgres.ExamQuestionRepository;
import com.cupcake.learning.exam.base.repository.postgres.ExamRepository;
import com.cupcake.learning.exam.question.repository.QuestionRepository;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ExamQuestionMutationResolver implements GraphQLMutationResolver {
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionRepository questionRepository;

    public ExamQuestionMutationResolver(ExamRepository examRepository,
                                        ExamQuestionRepository examQuestionRepository,
                                        QuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.questionRepository = questionRepository;
    }

    public List<ExamQuestion> setExamQuestions(UUID id, UUID authorId, List<UUID> questionIds) {
        if (!examRepository.existsByIdAndAuthorId(id, authorId))
            throw new RuntimeException("Unable to find given exam");

        // Check if questions exists.
        var missingQuestions = questionIds.stream()
                .filter(questionId -> !questionRepository.existsByIdAndAuthorId(questionId, authorId))
                .collect(Collectors.toList());
        if (!missingQuestions.isEmpty())
            throw new RuntimeException("Unable to find the following questions: " + missingQuestions);

        // Remove records not in input 'questionIds'
        List<ExamQuestion> removedQuestions = examQuestionRepository.findByIdExamId(id).stream()
                .filter(examQuestion -> !questionIds.contains(examQuestion.getId().getQuestionId()))
                .collect(Collectors.toList());
        examQuestionRepository.deleteAll(removedQuestions);

        // Update list with input 'questionIds'
        var examQuestions = new ArrayList<ExamQuestion>();
        for (int i = 0; i < questionIds.size(); i++) {
            UUID questionId = questionIds.get(i);
            var examQuestion = new ExamQuestion();
            var examQuestionId = new ExamQuestion.ExamQuestionId();
            examQuestionId.setExamId(id);
            examQuestionId.setQuestionId(questionId);

            examQuestion.setId(examQuestionId);
            examQuestion.setPositionIndex(i);
            examQuestions.add(examQuestion);
        }

        return examQuestionRepository.saveAll(examQuestions);
    }
}
