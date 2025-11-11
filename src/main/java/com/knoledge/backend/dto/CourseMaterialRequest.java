package com.knoledge.backend.dto;

import com.knoledge.backend.models.CourseMaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CourseMaterialRequest {

    @NotNull(message = "El tipo de material es obligatorio")
    private CourseMaterialType type;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150)
    private String title;

    @Size(max = 500)
    private String description;

    @Size(max = 500)
    private String resourceUrl;

    public CourseMaterialRequest() {
        // Constructor por defecto
    }

    public CourseMaterialType getType() {
        return type;
    }

    public void setType(CourseMaterialType type) {
        this.type = type;
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

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }
}
