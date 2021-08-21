package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.Exam;
import com.cupcake.learning.exam.util.PatchModelMapper;
import com.cupcake.learning.exam.auth.repository.UserRepository;
import com.cupcake.learning.exam.base.model.input.ExamInput;
import com.cupcake.learning.exam.base.repository.ExamRepository;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ExamMutationResolver implements GraphQLMutationResolver {
    private final PatchModelMapper mapper;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    public ExamMutationResolver(PatchModelMapper mapper, UserRepository userRepository, ExamRepository examRepository) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.examRepository = examRepository;
    }

    public Exam addExam(UUID authorId, ExamInput input) {
        userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Unable to find given user"));

        var exam = new Exam();
        mapper.map(input, exam);
        exam.setAuthorId(authorId);
        return examRepository.save(exam);
    }

    // TODO: Add following logic once we can get current user info.
    // If current user is normal user, check if authorId matches.
    public Exam updateExam(UUID id, UUID authorId, ExamInput input) {
        var exam = examRepository.findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new RuntimeException("Unable to find given exam"));
        mapper.map(input, exam);
        return examRepository.save(exam);
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
