package com.knoledge.backend.repositories;

import com.knoledge.backend.models.Course;
import com.knoledge.backend.models.CourseEnrollment;
import com.knoledge.backend.models.EnrollmentStatus;
import com.knoledge.backend.models.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    List<CourseEnrollment> findByCourse(Course course);

    List<CourseEnrollment> findByCourseAndStatus(Course course, EnrollmentStatus status);

    Optional<CourseEnrollment> findByCourseAndStudent(Course course, Usuario student);

    List<CourseEnrollment> findByStudent(Usuario student);

    long countByCourseAndStatus(Course course, EnrollmentStatus status);
}
