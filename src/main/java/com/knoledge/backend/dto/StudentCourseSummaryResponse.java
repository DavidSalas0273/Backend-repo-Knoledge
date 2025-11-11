package com.knoledge.backend.dto;

import com.knoledge.backend.models.CourseEnrollment;
import com.knoledge.backend.models.EnrollmentStatus;

public class StudentCourseSummaryResponse {

    private final Long id;
    private final String title;
    private final String category;
    private final String description;
    private final String teacherName;
    private final Integer durationMinutes;
    private final EnrollmentStatus status;

    public StudentCourseSummaryResponse(Long id, String title, String category,
            String description, String teacherName, Integer durationMinutes,
            EnrollmentStatus status) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.teacherName = teacherName;
        this.durationMinutes = durationMinutes;
        this.status = status;
    }

    public static StudentCourseSummaryResponse fromEnrollment(CourseEnrollment enrollment) {
        return new StudentCourseSummaryResponse(
                enrollment.getCourse().getId(),
                enrollment.getCourse().getTitle(),
                enrollment.getCourse().getCategory(),
                enrollment.getCourse().getDescription(),
                enrollment.getCourse().getTeacher() != null ? enrollment.getCourse().getTeacher().getNombre() : null,
                enrollment.getCourse().getDurationMinutes(),
                enrollment.getStatus()
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

    public String getTeacherName() {
        return teacherName;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }
}
