package com.knoledge.backend.dto;

import com.knoledge.backend.models.Course;
import java.time.LocalDateTime;
import java.util.List;

public class CourseResponse {

    private final Long id;
    private final String title;
    private final String category;
    private final String description;
    private final List<String> topics;
    private final Integer durationMinutes;
    private final String content;
    private final LocalDateTime createdAt;
    private final String joinCode;
    private final String teacherName;
    private final String teacherEmail;

    public CourseResponse(Long id, String title, String category, String description,
            List<String> topics, Integer durationMinutes, String content,
            LocalDateTime createdAt, String joinCode, String teacherName, String teacherEmail) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.topics = topics;
        this.durationMinutes = durationMinutes;
        this.content = content;
        this.createdAt = createdAt;
        this.joinCode = joinCode;
        this.teacherName = teacherName;
        this.teacherEmail = teacherEmail;
    }

    public static CourseResponse fromEntity(Course course) {
        String teacherName = course.getTeacher() != null ? course.getTeacher().getNombre() : null;
        String teacherEmail = course.getTeacher() != null ? course.getTeacher().getEmail() : null;
        return new CourseResponse(course.getId(), course.getTitle(), course.getCategory(),
                course.getDescription(), course.getTopics(), course.getDurationMinutes(),
                course.getContent(), course.getCreatedAt(), course.getJoinCode(), teacherName, teacherEmail);
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

    public List<String> getTopics() {
        return topics;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }
}
