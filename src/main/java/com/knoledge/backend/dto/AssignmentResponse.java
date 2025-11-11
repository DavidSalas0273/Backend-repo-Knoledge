package com.knoledge.backend.dto;

import com.knoledge.backend.models.Assignment;
import java.time.LocalDateTime;
import java.util.List;

public class AssignmentResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final LocalDateTime dueDate;
    private final LocalDateTime createdAt;
    private final List<AssignmentSubmissionResponse> submissions;

    public AssignmentResponse(Long id, String title, String description,
            LocalDateTime dueDate, LocalDateTime createdAt,
            List<AssignmentSubmissionResponse> submissions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.submissions = submissions;
    }

    public static AssignmentResponse fromEntity(Assignment assignment,
            List<AssignmentSubmissionResponse> submissions) {
        return new AssignmentResponse(
                assignment.getId(),
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDueDate(),
                assignment.getCreatedAt(),
                submissions
        );
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<AssignmentSubmissionResponse> getSubmissions() {
        return submissions;
    }
}
