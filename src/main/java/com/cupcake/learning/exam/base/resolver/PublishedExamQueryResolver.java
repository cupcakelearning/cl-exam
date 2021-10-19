package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.dynamo.PublishedExam;
import com.cupcake.learning.exam.base.model.entity.postgres.Exam;
import com.cupcake.learning.exam.base.model.entity.postgres.PublishedExamMetaData;
import com.cupcake.learning.exam.base.repository.dynamo.PublishedExamRepository;
import com.cupcake.learning.exam.base.repository.postgres.ExamRepository;
import com.cupcake.learning.exam.base.repository.postgres.PublishedExamMetaDataRepository;
import com.cupcake.learning.exam.util.CursorEncoder;
import com.cupcake.learning.exam.util.PatchModelMapper;
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
public class PublishedExamQueryResolver implements GraphQLQueryResolver {
    private final PatchModelMapper mapper;
    private final CursorEncoder cursorEncoder;
    private final PublishedExamMetaDataRepository publishedExamMetaDataRepository;
    private final PublishedExamRepository publishedExamRepository;

    public PublishedExamQueryResolver(PatchModelMapper mapper,
                                      CursorEncoder cursorEncoder,
                                      PublishedExamMetaDataRepository publishedExamMetaDataRepository,
                                      PublishedExamRepository publishedExamRepository) {
        this.mapper = mapper;
        this.cursorEncoder = cursorEncoder;
        this.publishedExamMetaDataRepository = publishedExamMetaDataRepository;
        this.publishedExamRepository = publishedExamRepository;
    }

    public Connection<PublishedExamMetaData> publishedExamsForSale(UUID authorId, int first, @Nullable String cursor) {
        Pageable pageable = PageRequest.of(0, first < 1 ? 20 : first);
        Page<PublishedExamMetaData> pageResult = getPublishedExamMetaData(authorId, cursor, pageable);

        List<Edge<PublishedExamMetaData>> edges = pageResult.getContent()
                .stream()
                .map(metaData -> new DefaultEdge<>(metaData, cursorEncoder.createCursorWith(metaData.getPublishedDateTime())))
                .collect(Collectors.toList());

        var pageInfo = new DefaultPageInfo(
                cursorEncoder.getFirstCursorFrom(edges),
                cursorEncoder.getLastCursorFrom(edges),
                false,
                pageResult.hasNext());

        return new DefaultConnection<>(edges, pageInfo);
    }

    private Page<PublishedExamMetaData> getPublishedExamMetaData(UUID authorId, String cursor, Pageable pageable) {
        boolean hasAuthor = authorId != null;

        if (cursor == null || cursor.isBlank()) {
            if (hasAuthor) {
                return publishedExamMetaDataRepository.findByAuthorIdAndIsActiveOrderByPublishedDateTimeDesc(authorId, true, pageable);
            }
            else {
                return publishedExamMetaDataRepository.findByIsActiveOrderByPublishedDateTimeDesc(true, pageable);
            }
        } else {
            if (hasAuthor) {
                return publishedExamMetaDataRepository.findByAuthorIdAndIsActiveAndPublishedDateTimeBeforeOrderByPublishedDateTimeDesc
                        (authorId, true, pageable, cursorEncoder.decodeDateTimeCursor(cursor));
            }
            else {
                return publishedExamMetaDataRepository.findByIsActiveAndPublishedDateTimeBeforeOrderByPublishedDateTimeDesc
                        (true, pageable, cursorEncoder.decodeDateTimeCursor(cursor));
            }
        }
    }

    public List<PublishedExamMetaData> getPublishedExamsForBaseExam(UUID examId) {
        return publishedExamMetaDataRepository.findByExamId(examId);
    }

    public PublishedExamMetaData getBaseExamForPublishedExam(UUID publishedExamId) {
        return publishedExamMetaDataRepository.findByPublishedExamId(publishedExamId)
                .orElseThrow(() -> new RuntimeException("Unable to find given published exam"));
    }

    public PublishedExam publishedExam(UUID publishedExamId) {
        return publishedExamRepository.findById(publishedExamId)
                .orElseThrow(() -> new RuntimeException("Unable to find given published exam"));
    }
}
