package com.knoledge.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Entity
@Table(name = "courses")
public class Course {

    private static final String TOPIC_DELIMITER = "||";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "topics", columnDefinition = "TEXT", nullable = false)
    private String topicsSerialized = "";

    @Column(name = "durationMinutes", nullable = false)
    private Integer durationMinutes = 60;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "contentHtml", columnDefinition = "TEXT")
    private String contentHtml = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Usuario teacher;

    @Column(nullable = false, unique = true, length = 12)
    private String joinCode;

    @Column(name = "code", nullable = false, unique = true, length = 12)
    private String code;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Course() {
        // Constructor por defecto
    }

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
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
        this.category = category != null ? category.trim() : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }

    public List<String> getTopics() {
        if (topicsSerialized == null || topicsSerialized.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(topicsSerialized.split(Pattern.quote(TOPIC_DELIMITER)))
                .map(String::trim)
                .filter(segment -> !segment.isEmpty())
                .collect(Collectors.toList());
    }

    public void setTopics(List<String> topics) {
        if (topics == null || topics.isEmpty()) {
            this.topicsSerialized = "";
            return;
        }
        this.topicsSerialized = topics.stream()
                .map(topic -> topic == null ? "" : topic.trim())
                .filter(segment -> !segment.isEmpty())
                .collect(Collectors.joining(TOPIC_DELIMITER));
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        if (durationMinutes == null || durationMinutes <= 0) {
            this.durationMinutes = 60;
        } else {
            this.durationMinutes = durationMinutes;
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content != null ? content.trim() : null;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml == null ? "" : contentHtml.trim();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Usuario getTeacher() {
        return teacher;
    }

    public void setTeacher(Usuario teacher) {
        this.teacher = teacher;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode != null ? joinCode.trim().toUpperCase(Locale.ROOT) : null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code != null ? code.trim().toUpperCase(Locale.ROOT) : null;
    }
}
