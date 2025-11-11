package com.knoledge.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class AssignmentRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150)
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    private LocalDateTime dueDate;

    public AssignmentRequest() {
        // Constructor por defecto
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
