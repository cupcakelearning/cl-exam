package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.postgres.Exam;
import com.cupcake.learning.exam.util.CursorUtil;
import com.cupcake.learning.exam.base.repository.postgres.ExamRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.relay.*;
import io.micrometer.core.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ExamQueryResolver implements GraphQLQueryResolver {
    private final CursorUtil cursorUtil;
    private final ExamRepository examRepository;

    public ExamQueryResolver(CursorUtil cursorUtil, ExamRepository examRepository) {
        this.cursorUtil = cursorUtil;
        this.examRepository = examRepository;
    }

    public Exam exam(UUID id) {
        return examRepository.findByIsActiveAndId(true, id)
                .orElseThrow(() -> new RuntimeException("Unable to find given exam"));
    }

    public Connection<Exam> exams(UUID authorId, int first, @Nullable String cursor) {
        Pageable pageable = PageRequest.of(0, first < 1 ? 20 : first);
        Page<Exam> pageResult;
        if (cursor == null || cursor.isBlank()) {
            pageResult = examRepository.findByAuthorIdAndIsActiveOrderByIdAsc(authorId, true, pageable);
        } else {
            pageResult = examRepository.findByAuthorIdAndIsActiveAndIdAfterOrderByIdAsc(authorId, true, pageable, cursorUtil.decode(cursor));
        }

        List<Edge<Exam>> edges = pageResult.getContent()
                .stream()
                .map(exam -> new DefaultEdge<>(exam, cursorUtil.createCursorWith(exam.getId())))
                .collect(Collectors.toList());

        var pageInfo = new DefaultPageInfo(
                cursorUtil.getFirstCursorFrom(edges),
                cursorUtil.getLastCursorFrom(edges),
                false,
                pageResult.hasNext());

        return new DefaultConnection<>(edges, pageInfo);
    }
}
