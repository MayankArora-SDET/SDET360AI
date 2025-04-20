package com.sdet.sdet360.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultDataSourceConfig {

    @Bean(name = "defaultDataSource")
    public DataSource defaultDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/mastersdet360");
        dataSource.setUsername("postgres");
        dataSource.setPassword("12345");
        dataSource.setMinimumIdle(2);
        dataSource.setMaximumPoolSize(10);
        return dataSource;
    }
}
