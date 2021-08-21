package com.cupcake.learning.exam.base.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "examQuestion", schema = "public")
public class ExamQuestion {
    @EmbeddedId
    private ExamQuestionId id;
    private Integer positionIndex;

    public ExamQuestionId getId() {
        return id;
    }

    public void setId(ExamQuestionId id) {
        this.id = id;
    }

    public int getPositionIndex() {
        return positionIndex;
    }

    public void setPositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }

    @Embeddable
    public static class ExamQuestionId implements Serializable {
        private UUID examId;
        private UUID questionId;

        public UUID getExamId() {
            return examId;
        }

        public void setExamId(UUID examId) {
            this.examId = examId;
        }

        public UUID getQuestionId() {
            return questionId;
        }

        public void setQuestionId(UUID questionId) {
            this.questionId = questionId;
        }
    }
}
