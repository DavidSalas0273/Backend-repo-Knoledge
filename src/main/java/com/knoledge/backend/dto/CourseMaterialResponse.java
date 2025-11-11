package com.knoledge.backend.dto;

import com.knoledge.backend.models.CourseMaterial;
import com.knoledge.backend.models.CourseMaterialType;
import java.time.LocalDateTime;

public class CourseMaterialResponse {

    private final Long id;
    private final CourseMaterialType type;
    private final String title;
    private final String description;
    private final String resourceUrl;
    private final String downloadUrl;
    private final LocalDateTime createdAt;

    public CourseMaterialResponse(Long id, CourseMaterialType type, String title, String description,
            String resourceUrl, String downloadUrl, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.resourceUrl = resourceUrl;
        this.downloadUrl = downloadUrl;
        this.createdAt = createdAt;
    }

    public static CourseMaterialResponse fromEntity(CourseMaterial material, String downloadUrl) {
        return new CourseMaterialResponse(
                material.getId(),
                material.getType(),
                material.getTitle(),
                material.getDescription(),
                material.getResourceUrl(),
                downloadUrl,
                material.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public CourseMaterialType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
