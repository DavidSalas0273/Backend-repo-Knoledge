package com.knoledge.backend.dto;

import com.knoledge.backend.models.AssignmentSubmission;
import java.time.LocalDateTime;

public class AssignmentSubmissionResponse {

    private final Long id;
    private final Long studentId;
    private final String studentName;
    private final String studentEmail;
    private final String fileUrl;
    private final LocalDateTime submittedAt;
    private final Double grade;
    private final String feedback;

    public AssignmentSubmissionResponse(Long id, Long studentId, String studentName,
            String studentEmail, String fileUrl, LocalDateTime submittedAt,
            Double grade, String feedback) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.fileUrl = fileUrl;
        this.submittedAt = submittedAt;
        this.grade = grade;
        this.feedback = feedback;
    }

    public static AssignmentSubmissionResponse fromEntity(AssignmentSubmission submission, String fileUrl) {
        return new AssignmentSubmissionResponse(
                submission.getId(),
                submission.getStudent().getId(),
                submission.getStudent().getNombre(),
                submission.getStudent().getEmail(),
                fileUrl,
                submission.getSubmittedAt(),
                submission.getGrade(),
                submission.getFeedback()
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

    public String getFileUrl() {
        return fileUrl;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public Double getGrade() {
        return grade;
    }

    public String getFeedback() {
        return feedback;
    }
}
