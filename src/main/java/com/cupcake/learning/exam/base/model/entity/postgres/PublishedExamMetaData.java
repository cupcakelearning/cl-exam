package com.cupcake.learning.exam.base.model.entity.postgres;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "publishedexam_to_metadata", schema = "public")
public class PublishedExamMetaData {
    @Id
    private UUID publishedExamId;
    private UUID examId;
    private UUID authorId;
    private String name;
    private String description;
    private BigDecimal price;
    private String subject;
    private Integer durationInMinutes;
    private OffsetDateTime publishedDateTime;
    private Boolean isActive;

    public UUID getPublishedExamId() {
        return publishedExamId;
    }

    public void setPublishedExamId(UUID publishedExamId) {
        this.publishedExamId = publishedExamId;
    }

    public UUID getExamId() {
        return examId;
    }

    public void setExamId(UUID examId) {
        this.examId = examId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public OffsetDateTime getPublishedDateTime() {
        return publishedDateTime;
    }

    public void setPublishedDateTime(OffsetDateTime publishedDateTime) {
        this.publishedDateTime = publishedDateTime;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
