package com.knoledge.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CourseRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no debe superar los 150 caracteres")
    private String title;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 100, message = "La categoría no debe superar los 100 caracteres")
    private String category;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotEmpty(message = "Debe indicar al menos un tema")
    private List<@Size(max = 160) String> topics;

    @Min(value = 1, message = "La duración debe ser mayor a 0")
    private Integer durationMinutes;

    @NotBlank(message = "El contenido es obligatorio")
    private String content;

    public CourseRequest() {
        // Constructor por defecto
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
