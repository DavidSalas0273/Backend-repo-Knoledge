package com.knoledge.backend.dto;

import java.util.List;

public class CourseDetailResponse {

    private final CourseResponse course;
    private final List<CourseMaterialResponse> materials;
    private final List<EnrollmentResponse> pendingEnrollments;
    private final List<EnrollmentResponse> approvedEnrollments;
    private final List<AssignmentResponse> assignments;

    public CourseDetailResponse(CourseResponse course,
            List<CourseMaterialResponse> materials,
            List<EnrollmentResponse> pendingEnrollments,
            List<EnrollmentResponse> approvedEnrollments,
            List<AssignmentResponse> assignments) {
        this.course = course;
        this.materials = materials;
        this.pendingEnrollments = pendingEnrollments;
        this.approvedEnrollments = approvedEnrollments;
        this.assignments = assignments;
    }

    public CourseResponse getCourse() {
        return course;
    }

    public List<CourseMaterialResponse> getMaterials() {
        return materials;
    }

    public List<EnrollmentResponse> getPendingEnrollments() {
        return pendingEnrollments;
    }

    public List<EnrollmentResponse> getApprovedEnrollments() {
        return approvedEnrollments;
    }

    public List<AssignmentResponse> getAssignments() {
        return assignments;
    }
}
