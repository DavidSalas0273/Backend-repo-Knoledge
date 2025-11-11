package com.knoledge.backend.repositories;

import com.knoledge.backend.models.Assignment;
import com.knoledge.backend.models.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByCourse(Course course);
}
