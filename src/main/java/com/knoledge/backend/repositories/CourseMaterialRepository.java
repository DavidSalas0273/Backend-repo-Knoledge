package com.knoledge.backend.repositories;

import com.knoledge.backend.models.Course;
import com.knoledge.backend.models.CourseMaterial;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {

    List<CourseMaterial> findByCourse(Course course);

    Optional<CourseMaterial> findByIdAndCourse(Long id, Course course);
}
