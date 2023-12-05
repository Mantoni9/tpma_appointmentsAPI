package com.m2olie.appointmentService.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class SQLRunner implements CommandLineRunner {

    private final DataSource dataSource;
    private final ResourceLoader resourceLoader;
    private final JdbcTemplate jdbcTemplate;

    public SQLRunner(DataSource dataSource, ResourceLoader resourceLoader, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.resourceLoader = resourceLoader;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // Path to init.sql File
        Resource resource = resourceLoader.getResource("classpath:data/sql/init.sql");

        // Check if database is empty
        if (isDatabaseEmpty()) {
            //execute init.sql
            try (Connection conn = dataSource.getConnection()) {
                ScriptUtils.executeSqlScript(conn, resource);
            }
        }
    }

    private boolean isDatabaseEmpty() {
        String sql = "SELECT COUNT(*) FROM appointment";
        int count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count == 0;
    }
}
