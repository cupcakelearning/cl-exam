package com.cupcake.learning.exam.question.model.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

public class QuestionTypeConverter implements DynamoDBTypeConverter<String, QuestionType> {

    @Override
    public String convert(QuestionType questionType) {
        return questionType.name();
    }

    @Override
    public QuestionType unconvert(String s) {
        return QuestionType.valueOf(s);
    }
}
