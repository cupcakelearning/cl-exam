package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.base.model.entity.dynamo.PublishedExam;
import com.cupcake.learning.exam.base.model.entity.dynamo.QuestionDoc;
import com.cupcake.learning.exam.base.model.entity.postgres.Exam;
import com.cupcake.learning.exam.base.model.entity.postgres.PublishedExamMetaData;
import com.cupcake.learning.exam.base.repository.dynamo.PublishedExamRepository;
import com.cupcake.learning.exam.base.repository.postgres.ExamQuestionRepository;
import com.cupcake.learning.exam.base.repository.postgres.ExamRepository;
import com.cupcake.learning.exam.base.repository.postgres.PublishedExamMetaDataRepository;
import com.cupcake.learning.exam.question.model.entity.Question;
import com.cupcake.learning.exam.question.repository.QuestionRepository;
import com.cupcake.learning.exam.util.PatchModelMapper;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PublishedExamMutationResolver implements GraphQLMutationResolver {
    private final PatchModelMapper mapper;
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final PublishedExamMetaDataRepository publishedExamMetaDataRepository;
    private final PublishedExamRepository publishedExamRepository;

    public PublishedExamMutationResolver(PatchModelMapper mapper,
                                         QuestionRepository questionRepository,
                                         ExamRepository examRepository,
                                         ExamQuestionRepository examQuestionRepository,
                                         PublishedExamMetaDataRepository publishedExamMetaDataRepository,
                                         PublishedExamRepository publishedExamRepository) {
        this.mapper = mapper;
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.publishedExamMetaDataRepository = publishedExamMetaDataRepository;
        this.publishedExamRepository = publishedExamRepository;
    }

    // TODO: records wise
    // 1. [DONE] Rename PublishedExamContent to PublishedExam
    // 2. [DONE] Rename ExamPublished to PublishedExamMetaData
    // 3. [DONE] Rename cancelPublishedExam to unpublishExam
    // 4. [DONE] Implement freeze / unfreeze for exam
    // 5. [DONE] Query for active published exams (to buy)
    // 6. [DONE] Query for published exam via exam id (& vice versa)
    // 7. Change freeze / unfreeze to delete exam. [ Soft-delete; User no longer have access but store in database]
    // 8. Add "active" checks for exams operations.
    // 9. Retrieve only "active" exams.

    // TODO: For question locking
    // 1. Numeric versioning for image upload. (uuid_version_fileName) [Question project]
    //      1.a. Delete current linked image.
    //      1.b. Upload new image.
    // 2. Copy images to "published" folder.
    //      2.a. Check if image exists.
    //      2.b. Add if not found.

    public PublishedExamMetaData publishExam(UUID examId, UUID authorId) {
        Exam exam = getExam(examId, authorId);
        List<QuestionDoc> questionDocs = getAndMapQuestions(exam);

        var publishedExam = new PublishedExam();
        mapper.map(exam, publishedExam);
        publishedExam.setQuestions(questionDocs);
        publishedExam.setId(null);  // To let database autogenerate

        PublishedExam savedContent = publishedExamRepository.save(publishedExam);
        return savePublishedExamMetaData(exam, savedContent.getId());
    }

    public UUID unpublishExam(UUID publishedExamId, UUID authorId) {
        var publishedExamMetaData = publishedExamMetaDataRepository.findByPublishedExamIdAndAuthorId(publishedExamId, authorId)
                .orElseThrow(() -> new RuntimeException("Unable to find given published exam"));

        publishedExamMetaData.setActive(false);
        publishedExamMetaDataRepository.save(publishedExamMetaData);
        return publishedExamId;
    }

    public PublishedExamMetaData republishExam(UUID publishedExamId, UUID authorId) {
        var publishedExamMetaData = publishedExamMetaDataRepository.findByPublishedExamIdAndAuthorId(publishedExamId, authorId)
                .orElseThrow(() -> new RuntimeException("Unable to find given published exam"));

        publishedExamMetaData.setActive(true);
        return publishedExamMetaDataRepository.save(publishedExamMetaData);
    }

    private Exam getExam(UUID id, UUID authorId) {
        var exam = examRepository.findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new RuntimeException("Unable to find given exam"));

        if (exam.getPrice() == null || exam.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            throw new RuntimeException("Exam's selling price has yet to be set");

        return exam;
    }

    private List<QuestionDoc> getAndMapQuestions(Exam exam) {
        List<UUID> questionIds = examQuestionRepository.findByIdExamId(exam.getId())
                .stream()
                .map(examQuestion -> examQuestion.getId().getQuestionId())
                .collect(Collectors.toList());

        List<Question> questions = questionRepository.findAllById(questionIds);

        return questions.stream()
                .map(question -> {
                    var questionDoc = new QuestionDoc();
                    mapper.map(question, questionDoc);
                    return questionDoc;
                })
                .collect(Collectors.toList());
    }

    private PublishedExamMetaData savePublishedExamMetaData(Exam exam, UUID publishedId) {
        var publishedExamMetaData = new PublishedExamMetaData();
        mapper.map(exam, publishedExamMetaData);
        publishedExamMetaData.setPublishedExamId(publishedId);
        publishedExamMetaData.setExamId(exam.getId());
        publishedExamMetaData.setPublishedDateTime(OffsetDateTime.now());
        publishedExamMetaData.setActive(true);
        return publishedExamMetaDataRepository.save(publishedExamMetaData);
    }
}
