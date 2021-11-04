package com.cupcake.learning.exam.base.resolver;

import com.cupcake.learning.exam.auth.repository.UserRepository;
import com.cupcake.learning.exam.base.repository.dynamo.PublishedExamRepository;
import com.cupcake.learning.exam.base.repository.postgres.ExamQuestionRepository;
import com.cupcake.learning.exam.base.repository.postgres.ExamRepository;
import com.cupcake.learning.exam.base.repository.postgres.PublishedExamMetaDataRepository;
import com.cupcake.learning.exam.question.repository.QuestionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;

public class GraphQLTest {
    // Mock away all repositories since we're testing GraphQl only
    @MockBean
    UserRepository userRepository;

    @MockBean
    ExamRepository examRepository;

    @MockBean
    QuestionRepository questionRepository;

    @MockBean
    ExamQuestionRepository examQuestionRepository;

    @MockBean
    PublishedExamRepository publishedExamRepository;

    @MockBean
    PublishedExamMetaDataRepository publishedExamMetaDataRepository;

    @Autowired
    private ObjectMapper mapper;

    ObjectNode toVariablesInput(Map<String, Object> input) {
        var variables = mapper.createObjectNode();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            variables.set(entry.getKey(), mapper.convertValue(entry.getValue(), JsonNode.class));
        }
        return variables;
    }
}
