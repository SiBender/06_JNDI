package com.foxminded.university.config;

import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcTemplateConfig {
    @Autowired
    DataSource dataSource;
    
    @Bean
    public JdbcTemplate jdbcTemplate() throws NamingException, SQLException {
        return new JdbcTemplate(dataSource);
    }
}
