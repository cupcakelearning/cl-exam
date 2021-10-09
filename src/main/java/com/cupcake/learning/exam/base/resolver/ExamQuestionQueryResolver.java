package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.postgres.Exam;
import com.cupcake.learning.exam.base.model.entity.postgres.ExamQuestion;
import com.cupcake.learning.exam.base.repository.postgres.ExamQuestionRepository;
import com.cupcake.learning.exam.base.repository.postgres.ExamRepository;
import com.cupcake.learning.exam.util.CursorEncoder;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.relay.*;
import io.micrometer.core.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ExamQuestionQueryResolver implements GraphQLQueryResolver {
    private final CursorEncoder cursorEncoder;
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;

    public ExamQuestionQueryResolver(CursorEncoder cursorEncoder,
                                     ExamRepository examRepository,
                                     ExamQuestionRepository examQuestionRepository) {
        this.cursorEncoder = cursorEncoder;
        this.examRepository = examRepository;
        this.examQuestionRepository = examQuestionRepository;
    }

    public List<ExamQuestion> examQuestions(UUID id) {
        if (!examRepository.existsByIsActiveAndId(true, id))
            throw new RuntimeException("Unable to find given exam");

        return examQuestionRepository.findByIdExamId(id);
    }

    public Connection<Exam> examsContainingQuestion(UUID questionId, int first, @Nullable String cursor) {
        Pageable pageable = PageRequest.of(0, first < 1 ? 20 : first);
        Page<ExamQuestion> pageResult;
        if (cursor == null || cursor.isBlank()) {
            pageResult = examQuestionRepository.findByIdQuestionIdOrderByIdExamIdAsc(questionId, pageable);
        } else {
            pageResult = examQuestionRepository.findByIdQuestionIdAndIdExamIdAfterOrderByIdExamIdAsc(questionId, pageable, cursorEncoder.decode(cursor));
        }

        List<Edge<Exam>> edges = pageResult.getContent()
                .stream()
                .map(examQuestion -> examRepository.findById(examQuestion.getId().getExamId()))
                .filter(Optional::isPresent)
                .map(optionalExam -> {
                    var exam = optionalExam.get();
                    return new DefaultEdge<>(exam, cursorEncoder.createCursorWith(exam.getId()));
                })
                .collect(Collectors.toList());

        var pageInfo = new DefaultPageInfo(
                cursorEncoder.getFirstCursorFrom(edges),
                cursorEncoder.getLastCursorFrom(edges),
                false,
                pageResult.hasNext());

        return new DefaultConnection<>(edges, pageInfo);
    }
}
