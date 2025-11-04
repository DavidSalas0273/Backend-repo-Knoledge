package com.knoledge.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.sql.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final String url = "jdbc:mysql://localhost:3306/knoledge";
    private final String user = "root";
    private final String password = ""; // si tu MySQL tiene contraseña, ponla aquí

    @GetMapping
    public List<String> testConnection() {
        List<String> messages = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM test_connection")) {

            while (rs.next()) {
                messages.add(rs.getString("message"));
            }
        } catch (Exception e) {
            messages.add("❌ Error de conexión: " + e.getMessage());
        }

        if (messages.isEmpty()) {
            messages.add("✅ Conexión exitosa, pero no hay datos en la tabla.");
        }

        return messages;
    }
}
