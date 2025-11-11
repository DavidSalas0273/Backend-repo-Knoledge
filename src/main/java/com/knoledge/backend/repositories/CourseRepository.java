package com.knoledge.backend.repositories;

import com.knoledge.backend.models.Course;
import com.knoledge.backend.models.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByIdAndTeacher(Long id, Usuario teacher);

    Optional<Course> findByJoinCode(String joinCode);

    List<Course> findByTeacherOrderByCreatedAtDesc(Usuario teacher);
}
