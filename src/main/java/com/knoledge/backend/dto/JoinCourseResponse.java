package com.knoledge.backend.dto;

import com.knoledge.backend.models.EnrollmentStatus;

public class JoinCourseResponse {

    private final Long courseId;
    private final EnrollmentStatus status;
    private final String message;

    public JoinCourseResponse(Long courseId, EnrollmentStatus status, String message) {
        this.courseId = courseId;
        this.status = status;
        this.message = message;
    }

    public Long getCourseId() {
        return courseId;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
