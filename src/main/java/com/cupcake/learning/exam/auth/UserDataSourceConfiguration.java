package com.cupcake.learning.exam.auth;

import com.cupcake.learning.exam.auth.model.User;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.cupcake.learning.exam.auth.repository",
        entityManagerFactoryRef = "userEntityManager",
        transactionManagerRef = "userTransactionManager")
public class UserDataSourceConfiguration {
    @Bean
    @Primary
    @ConfigurationProperties("spring.user-datasource")
    public DataSourceProperties userDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.user-datasource.configuration")
    public DataSource userDataSource() {
        return userDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    // userEntityManager bean
    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean userEntityManager(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(userDataSource())
                .packages(User.class)
                .build();
    }


    // userTransactionManager bean
    @Primary
    @Bean
    public PlatformTransactionManager userTransactionManager(
            final @Qualifier("userEntityManager") LocalContainerEntityManagerFactoryBean userEntityManager) {
        return new JpaTransactionManager(Objects.requireNonNull(userEntityManager.getObject()));
    }

}