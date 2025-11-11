package com.knoledge.backend.repositories;

import com.knoledge.backend.models.Assignment;
import com.knoledge.backend.models.AssignmentSubmission;
import com.knoledge.backend.models.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    List<AssignmentSubmission> findByAssignment(Assignment assignment);

    Optional<AssignmentSubmission> findByAssignmentAndStudent(Assignment assignment, Usuario student);
}
