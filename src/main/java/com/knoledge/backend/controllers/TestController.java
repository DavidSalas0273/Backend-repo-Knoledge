package com.knoledge.backend.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final DataSource dataSource;

    public TestController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public Map<String, Object> testConnection() {
        try (Connection connection = dataSource.getConnection()) {
            boolean valid = connection.isValid(2);
            return Map.of(
                    "status", valid ? "ok" : "invalid",
                    "timestamp", Instant.now().toString()
            );
        } catch (SQLException ex) {
            return Map.of(
                    "status", "error",
                    "message", ex.getMessage(),
                    "timestamp", Instant.now().toString()
            );
        }
    }
}
