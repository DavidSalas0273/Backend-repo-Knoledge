package com.knoledge.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class JoinCourseRequest {

    @NotBlank(message = "El código es obligatorio")
    @Size(min = 4, max = 12)
    private String code;

    public JoinCourseRequest() {
        // Constructor por defecto
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
