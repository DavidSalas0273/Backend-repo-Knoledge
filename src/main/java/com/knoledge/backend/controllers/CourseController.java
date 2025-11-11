package com.knoledge.backend.controllers;

import com.knoledge.backend.dto.AssignmentRequest;
import com.knoledge.backend.dto.AssignmentResponse;
import com.knoledge.backend.dto.AssignmentSubmissionResponse;
import com.knoledge.backend.dto.CourseDetailResponse;
import com.knoledge.backend.dto.CourseMaterialRequest;
import com.knoledge.backend.dto.CourseMaterialResponse;
import com.knoledge.backend.dto.CourseRequest;
import com.knoledge.backend.dto.CourseResponse;
import com.knoledge.backend.dto.EnrollmentResponse;
import com.knoledge.backend.dto.GradeSubmissionRequest;
import com.knoledge.backend.dto.JoinCourseRequest;
import com.knoledge.backend.dto.JoinCourseResponse;
import com.knoledge.backend.dto.StudentCourseSummaryResponse;
import com.knoledge.backend.dto.TeacherCourseSummaryResponse;
import com.knoledge.backend.models.CourseMaterialType;
import com.knoledge.backend.models.Usuario;
import com.knoledge.backend.services.CourseService;
import com.knoledge.backend.services.CurrentUserService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/courses")
@CrossOrigin(origins = "${CLIENT_ORIGIN:http://localhost:5173}", allowCredentials = "true")
public class CourseController {

    private final CourseService courseService;
    private final CurrentUserService currentUserService;

    public CourseController(CourseService courseService, CurrentUserService currentUserService) {
        this.courseService = courseService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CourseRequest request) {
        Usuario teacher = currentUserService.requireUser(authorization);
        if (!"teacher".equalsIgnoreCase(teacher.getRole())) {
            return ResponseEntity.status(403).build();
        }
        CourseResponse response = courseService.createCourse(teacher, request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{courseId}/teacher")
    public CourseDetailResponse getCourseDetailForTeacher(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authorization) {
        Usuario teacher = currentUserService.requireUser(authorization);
        return courseService.getCourseDetailForTeacher(courseId, teacher);
    }

    @GetMapping("/teacher")
    public List<TeacherCourseSummaryResponse> getCoursesForTeacher(
            @RequestHeader("Authorization") String authorization) {
        Usuario teacher = currentUserService.requireUser(authorization);
        if (!"teacher".equalsIgnoreCase(teacher.getRole())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "No autorizado");
        }
        return courseService.getCoursesForTeacher(teacher);
    }

    @GetMapping("/{courseId}/student")
    public CourseDetailResponse getCourseDetailForStudent(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authorization) {
        Usuario student = currentUserService.requireUser(authorization);
        return courseService.getCourseDetailForStudent(courseId, student);
    }

    @GetMapping("/student")
    public List<StudentCourseSummaryResponse> getCoursesForStudent(
            @RequestHeader("Authorization") String authorization) {
        Usuario student = currentUserService.requireUser(authorization);
        if (!"student".equalsIgnoreCase(student.getRole())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "No autorizado");
        }
        return courseService.getCoursesForStudent(student);
    }

    @PostMapping("/{courseId}/materials")
    public CourseMaterialResponse addMaterial(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authorization,
            @RequestPart("metadata") @Valid CourseMaterialRequest metadata,
            @RequestPart(name = "file", required = false) MultipartFile file) throws IOException {
        Usuario teacher = currentUserService.requireUser(authorization);
        if (!"teacher".equalsIgnoreCase(teacher.getRole())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "No autorizado");
        }
        if (metadata.getType() == CourseMaterialType.DOCUMENT_FILE && file == null) {
            throw new IllegalArgumentException("Debes adjuntar un archivo");
        }
        return courseService.addMaterial(courseId, teacher, metadata, file);
    }

    @DeleteMapping("/{courseId}/materials/{materialId}")
    public ResponseEntity<Void> deleteMaterial(
            @PathVariable Long courseId,
            @PathVariable Long materialId,
            @RequestHeader("Authorization") String authorization) {
        Usuario teacher = currentUserService.requireUser(authorization);
        if (!"teacher".equalsIgnoreCase(teacher.getRole())) {
            return ResponseEntity.status(403).build();
        }
        courseService.removeMaterial(courseId, materialId, teacher);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authorization) {
        Usuario teacher = currentUserService.requireUser(authorization);
        if (!"teacher".equalsIgnoreCase(teacher.getRole())) {
            return ResponseEntity.status(403).build();
        }
        courseService.deleteCourse(courseId, teacher);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/join")
    public ResponseEntity<JoinCourseResponse> joinCourse(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody JoinCourseRequest request) {
        Usuario student = currentUserService.requireUser(authorization);
        if (!"student".equalsIgnoreCase(student.getRole())) {
            return ResponseEntity.status(403)
                    .body(new JoinCourseResponse(null, null, "Solo los estudiantes pueden unirse a clases"));
        }
        JoinCourseResponse response = courseService.requestEnrollment(request, student);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/assignments")
    public AssignmentResponse createAssignment(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody AssignmentRequest request) {
        Usuario teacher = currentUserService.requireUser(authorization);
        if (!"teacher".equalsIgnoreCase(teacher.getRole())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "No autorizado");
        }
        return courseService.createAssignment(courseId, teacher, request);
    }

    @PutMapping("/assignments/{assignmentId}")
    public AssignmentResponse updateAssignment(
            @PathVariable Long assignmentId,
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody AssignmentRequest request) {
        Usuario teacher = currentUserService.requireUser(authorization);
        if (!"teacher".equalsIgnoreCase(teacher.getRole())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "No autorizado");
        }
        return courseService.updateAssignment(assignmentId, teacher, request);
    }

    @PostMapping("/assignments/{assignmentId}/submissions")
    public AssignmentSubmissionResponse submitAssignment(
            @PathVariable Long assignmentId,
            @RequestHeader("Authorization") String authorization,
            @RequestPart("file") MultipartFile file) throws IOException {
        Usuario student = currentUserService.requireUser(authorization);
        if (!"student".equalsIgnoreCase(student.getRole())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "No autorizado");
        }
        return courseService.submitAssignment(assignmentId, student, file);
    }

    @PutMapping("/assignments/{assignmentId}/submissions/{submissionId}/grade")
    public AssignmentSubmissionResponse gradeSubmission(
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId,
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody GradeSubmissionRequest request) {
        Usuario teacher = currentUserService.requireUser(authorization);
        if (!"teacher".equalsIgnoreCase(teacher.getRole())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "No autorizado");
        }
        return courseService.gradeSubmission(assignmentId, submissionId, teacher, request);
    }

    @PostMapping("/enrollments/{enrollmentId}/approve")
    public EnrollmentResponse approveEnrollment(
            @PathVariable Long enrollmentId,
            @RequestHeader("Authorization") String authorization) {
        Usuario teacher = currentUserService.requireUser(authorization);
        if (!"teacher".equalsIgnoreCase(teacher.getRole())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "No autorizado");
        }
        return courseService.approveEnrollment(enrollmentId, teacher, true);
    }

    @PostMapping("/enrollments/{enrollmentId}/reject")
    public EnrollmentResponse rejectEnrollment(
            @PathVariable Long enrollmentId,
            @RequestHeader("Authorization") String authorization) {
        Usuario teacher = currentUserService.requireUser(authorization);
        if (!"teacher".equalsIgnoreCase(teacher.getRole())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "No autorizado");
        }
        return courseService.approveEnrollment(enrollmentId, teacher, false);
    }

    @DeleteMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<Void> removeEnrollment(
            @PathVariable Long enrollmentId,
            @RequestHeader("Authorization") String authorization) {
        Usuario teacher = currentUserService.requireUser(authorization);
        if (!"teacher".equalsIgnoreCase(teacher.getRole())) {
            return ResponseEntity.status(403).build();
        }
        courseService.removeEnrollment(enrollmentId, teacher);
        return ResponseEntity.noContent().build();
    }
}
