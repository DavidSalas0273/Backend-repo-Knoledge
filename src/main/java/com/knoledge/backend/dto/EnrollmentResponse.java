package com.knoledge.backend.dto;

import com.knoledge.backend.models.CourseEnrollment;
import com.knoledge.backend.models.EnrollmentStatus;
import java.time.LocalDateTime;

public class EnrollmentResponse {

    private final Long id;
    private final Long studentId;
    private final String studentName;
    private final String studentEmail;
    private final EnrollmentStatus status;
    private final LocalDateTime createdAt;

    public EnrollmentResponse(Long id, Long studentId, String studentName, String studentEmail,
            EnrollmentStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static EnrollmentResponse fromEntity(CourseEnrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getNombre(),
                enrollment.getStudent().getEmail(),
                enrollment.getStatus(),
                enrollment.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
