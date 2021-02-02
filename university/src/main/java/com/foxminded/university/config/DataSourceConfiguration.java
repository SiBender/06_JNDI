package com.foxminded.university.config;

import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:jndi.properties")
public class DataSourceConfiguration {
    @Value("${datasource.name}")
    private String jndiConnectionPoolName;
    
    @Bean
    public DataSource dataSource() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        Context environment = (Context) context.lookup("java:/comp/env/");
        DataSource dataSource = (DataSource) environment.lookup(jndiConnectionPoolName);
        return dataSource; 
    } 
}
