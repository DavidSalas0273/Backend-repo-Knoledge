package com.knoledge.backend.dto;

import com.knoledge.backend.models.Course;
import java.time.LocalDateTime;

public class TeacherCourseSummaryResponse {

    private final Long id;
    private final String title;
    private final String category;
    private final String description;
    private final Integer durationMinutes;
    private final String joinCode;
    private final LocalDateTime createdAt;
    private final long pendingEnrollments;
    private final long approvedEnrollments;

    public TeacherCourseSummaryResponse(Long id, String title, String category, String description,
            Integer durationMinutes, String joinCode, LocalDateTime createdAt,
            long pendingEnrollments, long approvedEnrollments) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.joinCode = joinCode;
        this.createdAt = createdAt;
        this.pendingEnrollments = pendingEnrollments;
        this.approvedEnrollments = approvedEnrollments;
    }

    public static TeacherCourseSummaryResponse fromEntity(Course course,
            long pending, long approved) {
        return new TeacherCourseSummaryResponse(
                course.getId(),
                course.getTitle(),
                course.getCategory(),
                course.getDescription(),
                course.getDurationMinutes(),
                course.getJoinCode(),
                course.getCreatedAt(),
                pending,
                approved
        );
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public long getPendingEnrollments() {
        return pendingEnrollments;
    }

    public long getApprovedEnrollments() {
        return approvedEnrollments;
    }
}
