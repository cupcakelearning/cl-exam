package com.cupcake.learning.exam.base;

import com.cupcake.learning.exam.base.model.entity.Exam;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.cupcake.learning.exam.base.repository",
        entityManagerFactoryRef = "examEntityManager",
        transactionManagerRef = "examTransactionManager")
public class ExamDataSourceConfiguration {
    @Bean
    @ConfigurationProperties("spring.exam-datasource")
    public DataSourceProperties examDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.exam-datasource.configuration")
    public DataSource examDataSource() {
        return examDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    // examEntityManager bean
    @Bean
    public LocalContainerEntityManagerFactoryBean examEntityManager(EntityManagerFactoryBuilder builder) {
        var properties = new HashMap<String, Object>();
        properties.put("generate-ddl", true);
        properties.put("hibernate.ddl-auto", "create");

        return builder
                .dataSource(examDataSource())
                .packages(Exam.class)
                .build();
    }

    // examTransactionManager bean
    @Bean
    public PlatformTransactionManager examTransactionManager(final @Qualifier("examEntityManager") LocalContainerEntityManagerFactoryBean examEntityManager) {
        return new JpaTransactionManager(Objects.requireNonNull(examEntityManager.getObject()));
    }
}