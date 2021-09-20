package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.auth.repository.UserRepository;
import com.cupcake.learning.exam.base.model.entity.Exam;
import com.cupcake.learning.exam.base.model.input.ExamInput;
import com.cupcake.learning.exam.base.repository.ExamQuestionRepository;
import com.cupcake.learning.exam.base.repository.ExamRepository;
import com.cupcake.learning.exam.util.PatchModelMapper;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ExamMutationResolver implements GraphQLMutationResolver {
    private final PatchModelMapper mapper;
    private final UserRepository userRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamRepository examRepository;

    public ExamMutationResolver(PatchModelMapper mapper,
                                UserRepository userRepository,
                                ExamQuestionRepository examQuestionRepository,
                                ExamRepository examRepository) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.examRepository = examRepository;
    }

    public Exam addExam(UUID authorId, ExamInput input) {
        if (!userRepository.existsById(authorId))
            throw new RuntimeException("Unable to find given user");

        var exam = new Exam();
        mapper.map(input, exam);
        exam.setAuthorId(authorId);
        return examRepository.save(exam);
    }

    // TODO: Add following logic once we can get current user info.
    // If current user is normal user, check if authorId matches.
    // Affected: updateExam, publishExam, cancelExam
    public Exam updateExam(UUID id, UUID authorId, ExamInput input) {
        var exam = examRepository.findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new RuntimeException("Unable to find given exam"));
        mapper.map(input, exam);
        return examRepository.save(exam);
    }

    // TODO: Refund if someone bought the exam + if exam is part of book
    @Transactional
    public UUID deleteExam(UUID id, UUID authorId) {
        var exam = examRepository.findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new RuntimeException("Unable to find given exam"));

        var examQuestions = examQuestionRepository.findByIdExamId(id);
        examQuestionRepository.deleteAll(examQuestions);
        return id;
    }

    public Exam publishExam(UUID id, UUID authorId) {
        var exam = examRepository.findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new RuntimeException("Unable to find given exam"));
        exam.setActive(true);
        return examRepository.save(exam);
    }

    public Exam cancelExam(UUID id, UUID authorId) {
        var exam = examRepository.findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new RuntimeException("Unable to find given exam"));
        exam.setActive(false);
        return examRepository.save(exam);
    }
}
