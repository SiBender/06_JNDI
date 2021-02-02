package com.foxminded.university.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySource("classpath:databaseConfig.properties")
public class DataSourceConfiguration {
    @Value("${jdbc.Url}")
    private String jdbcURL;
    @Value("${db.username}")
    private String dbUsername;
    @Value("${db.password}")
    private String dpPassword;
    @Value("${jdbc.driverClassName}")
    private String jdbcDriverClassName;
    
    @Value("${cachePrepStmts}")
    private String cachePrepStmts;
    @Value("${prepStmtCacheSize}")
    private String prepStmtCacheSize;
    @Value("${prepStmtCacheSqlLimit}")
    private String prepStmtCacheSqlLimit;
    @Value("${maximumIdle}")
    private String setMaximumIdle;
    
    @Bean
    public DataSource dataSource() throws SQLException {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(jdbcURL);
        config.setUsername(dbUsername);
        config.setPassword(dpPassword);
        config.setDriverClassName(jdbcDriverClassName);
        config.addDataSourceProperty("cachePrepStmts", cachePrepStmts);
        config.addDataSourceProperty("prepStmtCacheSize", prepStmtCacheSize);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", prepStmtCacheSqlLimit);
        config.addDataSourceProperty("setMaximumIdle", setMaximumIdle);

        return new HikariDataSource(config);
    }
}
