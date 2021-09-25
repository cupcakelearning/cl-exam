package com.cupcake.learning.exam.question.model.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.List;
import java.util.UUID;

@DynamoDBTable(tableName = "question")
public class Question {
    private UUID id;
    private UUID authorId;
    private QuestionType type;
    private String text;
    private String diagramLink;
    private String subject;
    private Integer level;
    private List<Option> options;

    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @DynamoDBAttribute
    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    @DynamoDBTypeConverted(converter = QuestionTypeConverter.class)
    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    @DynamoDBAttribute
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @DynamoDBAttribute
    public String getDiagramLink() {
        return diagramLink;
    }

    public void setDiagramLink(String diagramLink) {
        this.diagramLink = diagramLink;
    }

    @DynamoDBAttribute
    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    @DynamoDBAttribute
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @DynamoDBAttribute
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
