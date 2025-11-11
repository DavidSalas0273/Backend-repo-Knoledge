package com.knoledge.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GradeSubmissionRequest {

    @NotNull(message = "La nota es obligatoria")
    @Min(0)
    @Max(100)
    private Double grade;

    @Size(max = 500)
    private String feedback;

    public GradeSubmissionRequest() {
        // Constructor por defecto
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
