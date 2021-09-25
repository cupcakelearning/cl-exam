package com.cupcake.learning.exam.base;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphqlScalarConfiguration {
    @Bean
    public GraphQLScalarType jsonType() {
        return ExtendedScalars.DateTime;
    }
}
