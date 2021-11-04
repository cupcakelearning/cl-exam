package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.dynamo.QuestionDoc;
import com.cupcake.learning.exam.base.model.entity.postgres.Exam;
import com.cupcake.learning.exam.base.model.entity.postgres.ExamQuestion;
import com.cupcake.learning.exam.base.repository.postgres.ExamQuestionRepository;
import com.cupcake.learning.exam.base.repository.postgres.ExamRepository;
import com.cupcake.learning.exam.question.repository.QuestionRepository;
import com.cupcake.learning.exam.util.CursorUtil;
import com.cupcake.learning.exam.util.PatchModelMapper;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.relay.*;
import io.micrometer.core.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExamQuestionQueryResolver implements GraphQLQueryResolver {
    private final PatchModelMapper mapper;
    private final CursorUtil cursorUtil;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamQuestionRepository examQuestionRepository;

    public ExamQuestionQueryResolver(PatchModelMapper mapper,
                                     CursorUtil cursorUtil,
                                     ExamRepository examRepository,
                                     QuestionRepository questionRepository,
                                     ExamQuestionRepository examQuestionRepository) {
        this.mapper = mapper;
        this.cursorUtil = cursorUtil;
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.examQuestionRepository = examQuestionRepository;
    }

    public List<ExamQuestion> examQuestions(UUID id) {
        if (!examRepository.existsByIsActiveAndId(true, id))
            throw new RuntimeException("Unable to find given exam");

        return examQuestionRepository.findByIdExamId(id);
    }

    public Connection<QuestionDoc> examQuestionObjects(UUID id, int first, @Nullable String cursor) {
        if (!examRepository.existsByIsActiveAndId(true, id))
            throw new RuntimeException("Unable to find given exam");

        Pageable pageable = PageRequest.of(0, first < 1 ? 20 : first);
        Page<ExamQuestion> pageResult;
        if (cursor == null || cursor.isBlank()) {
            pageResult = examQuestionRepository.findByIdExamIdOrderByPositionIndexAsc(id, pageable);
        } else {
            pageResult = examQuestionRepository.findByIdExamIdAndPositionIndexAfterOrderByPositionIndexAsc(id, pageable, cursorUtil.decodeIntegerCursor(cursor));
        }

        List<Edge<QuestionDoc>> edges = pageResult.getContent()
                .stream()
                .map(examQuestion -> {
                    return questionRepository.findById(examQuestion.getId().getQuestionId())
                            .map(questionResult -> {
                                var questionDoc = new QuestionDoc();
                                mapper.map(questionResult, questionDoc);
                                return Map.entry(examQuestion.getPositionIndex(), questionDoc);
                            })
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .map(entry -> new DefaultEdge<>(entry.getValue(), cursorUtil.createCursorWith(entry.getKey())))
                .collect(Collectors.toList());

        var pageInfo = new DefaultPageInfo(
                cursorUtil.getFirstCursorFrom(edges),
                cursorUtil.getLastCursorFrom(edges),
                false,
                pageResult.hasNext());

        return new DefaultConnection<>(edges, pageInfo);
    }

    public Connection<Exam> examsContainingQuestion(UUID questionId, int first, @Nullable String cursor) {
        Pageable pageable = PageRequest.of(0, first < 1 ? 20 : first);
        Page<ExamQuestion> pageResult;
        if (cursor == null || cursor.isBlank()) {
            pageResult = examQuestionRepository.findByIdQuestionIdOrderByIdExamIdAsc(questionId, pageable);
        } else {
            pageResult = examQuestionRepository.findByIdQuestionIdAndIdExamIdAfterOrderByIdExamIdAsc(questionId, pageable, cursorUtil.decode(cursor));
        }

        List<Edge<Exam>> edges = pageResult.getContent()
                .stream()
                .map(examQuestion -> examRepository.findById(examQuestion.getId().getExamId()))
                .filter(Optional::isPresent)
                .map(optionalExam -> {
                    var exam = optionalExam.get();
                    return new DefaultEdge<>(exam, cursorUtil.createCursorWith(exam.getId()));
                })
                .collect(Collectors.toList());

        var pageInfo = new DefaultPageInfo(
                cursorUtil.getFirstCursorFrom(edges),
                cursorUtil.getLastCursorFrom(edges),
                false,
                pageResult.hasNext());

        return new DefaultConnection<>(edges, pageInfo);
    }
}
