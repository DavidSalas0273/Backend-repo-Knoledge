package com.knoledge.backend.services;

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
import com.knoledge.backend.models.Assignment;
import com.knoledge.backend.models.AssignmentSubmission;
import com.knoledge.backend.models.Course;
import com.knoledge.backend.models.CourseEnrollment;
import com.knoledge.backend.models.CourseMaterial;
import com.knoledge.backend.models.CourseMaterialType;
import com.knoledge.backend.models.EnrollmentStatus;
import com.knoledge.backend.models.Usuario;
import com.knoledge.backend.repositories.AssignmentRepository;
import com.knoledge.backend.repositories.AssignmentSubmissionRepository;
import com.knoledge.backend.repositories.CourseEnrollmentRepository;
import com.knoledge.backend.repositories.CourseMaterialRepository;
import com.knoledge.backend.repositories.CourseRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CourseService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final CourseRepository courseRepository;
    private final CourseMaterialRepository courseMaterialRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
    private final FileStorageService fileStorageService;

    public CourseService(
            CourseRepository courseRepository,
            CourseMaterialRepository courseMaterialRepository,
            CourseEnrollmentRepository courseEnrollmentRepository,
            AssignmentRepository assignmentRepository,
            AssignmentSubmissionRepository assignmentSubmissionRepository,
            FileStorageService fileStorageService) {
        this.courseRepository = courseRepository;
        this.courseMaterialRepository = courseMaterialRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public CourseResponse createCourse(Usuario teacher, CourseRequest request) {
        Course course = new Course();
        course.setTeacher(teacher);
        String title = sanitizeOrFallback(request.getTitle(), "Curso sin título");
        course.setTitle(title);
        course.setCategory(sanitizeOrFallback(request.getCategory(), "general"));
        course.setDescription(sanitizeOrFallback(request.getDescription(), "Descripción pendiente"));

        List<String> requestedTopics = request.getTopics() == null ? List.of(title) : request.getTopics();
        List<String> topics = requestedTopics.stream()
                .map(topic -> topic == null ? "" : topic.trim())
                .filter(topic -> !topic.isEmpty())
                .collect(Collectors.toList());
        if (topics.isEmpty()) {
            topics = List.of(title);
        }
        course.setTopics(topics);

        Integer durationMinutes = request.getDurationMinutes();
        if (durationMinutes == null || durationMinutes <= 0) {
            durationMinutes = Math.max(60, topics.size() * 30);
        }
        course.setDurationMinutes(durationMinutes);

        String content = sanitizeOrFallback(request.getContent(), "Contenido pendiente por definir");
        course.setContent(content);
        course.setContentHtml(content);
        String joinCode = generateUniqueJoinCode();
        course.setJoinCode(joinCode);
        course.setCode(joinCode);
        course.setCreatedAt(java.time.LocalDateTime.now());

        Course saved = courseRepository.save(course);
        return CourseResponse.fromEntity(saved);
    }

    @Transactional
    public List<TeacherCourseSummaryResponse> getCoursesForTeacher(Usuario teacher) {
        return courseRepository.findByTeacherOrderByCreatedAtDesc(teacher)
                .stream()
                .map(course -> {
                    long pending = courseEnrollmentRepository.countByCourseAndStatus(course, EnrollmentStatus.PENDING);
                    long approved = courseEnrollmentRepository.countByCourseAndStatus(course, EnrollmentStatus.APPROVED);
                    return TeacherCourseSummaryResponse.fromEntity(course, pending, approved);
                })
                .collect(Collectors.toList());
    }

    private String sanitizeOrFallback(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
    }

    @Transactional
    public CourseDetailResponse getCourseDetailForTeacher(Long courseId, Usuario teacher) {
        Course course = courseRepository.findByIdAndTeacher(courseId, teacher)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        List<CourseMaterialResponse> materials = courseMaterialRepository.findByCourse(course)
                .stream()
                .sorted(Comparator.comparing(CourseMaterial::getCreatedAt).reversed())
                .map(material -> CourseMaterialResponse.fromEntity(material, buildFileUrl(material.getFilePath())))
                .collect(Collectors.toList());

        List<EnrollmentResponse> pending = courseEnrollmentRepository
                .findByCourseAndStatus(course, EnrollmentStatus.PENDING)
                .stream()
                .map(EnrollmentResponse::fromEntity)
                .collect(Collectors.toList());

        List<EnrollmentResponse> approved = courseEnrollmentRepository
                .findByCourseAndStatus(course, EnrollmentStatus.APPROVED)
                .stream()
                .map(EnrollmentResponse::fromEntity)
                .collect(Collectors.toList());

        List<AssignmentResponse> assignments = assignmentRepository.findByCourse(course)
                .stream()
                .sorted(Comparator.comparing(Assignment::getCreatedAt).reversed())
                .map(assignment -> {
                    List<AssignmentSubmissionResponse> submissions = assignmentSubmissionRepository
                            .findByAssignment(assignment)
                            .stream()
                            .sorted(Comparator.comparing(AssignmentSubmission::getSubmittedAt).reversed())
                            .map(submission -> AssignmentSubmissionResponse.fromEntity(
                                    submission, buildFileUrl(submission.getFilePath())))
                            .collect(Collectors.toList());
                    return AssignmentResponse.fromEntity(assignment, submissions);
                })
                .collect(Collectors.toList());

        CourseResponse courseResponse = CourseResponse.fromEntity(course);
        return new CourseDetailResponse(courseResponse, materials, pending, approved, assignments);
    }

    @Transactional
    public CourseDetailResponse getCourseDetailForStudent(Long courseId, Usuario student) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        CourseEnrollment enrollment = courseEnrollmentRepository.findByCourseAndStudent(course, student)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "No estás inscrito en este curso"));

        if (enrollment.getStatus() != EnrollmentStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Tu inscripción aún no ha sido aprobada");
        }

        List<CourseMaterialResponse> materials = courseMaterialRepository.findByCourse(course)
                .stream()
                .sorted(Comparator.comparing(CourseMaterial::getCreatedAt).reversed())
                .map(material -> CourseMaterialResponse.fromEntity(material, buildFileUrl(material.getFilePath())))
                .collect(Collectors.toList());

        List<AssignmentResponse> assignments = assignmentRepository.findByCourse(course)
                .stream()
                .sorted(Comparator.comparing(Assignment::getCreatedAt).reversed())
                .map(assignment -> {
                    Optional<AssignmentSubmission> submissionOptional =
                            assignmentSubmissionRepository.findByAssignmentAndStudent(assignment, student);
                    List<AssignmentSubmissionResponse> submissions = submissionOptional
                            .map(submission -> List.of(AssignmentSubmissionResponse.fromEntity(
                                    submission, buildFileUrl(submission.getFilePath()))))
                            .orElseGet(List::of);
                    return AssignmentResponse.fromEntity(assignment, submissions);
                })
                .collect(Collectors.toList());

        CourseResponse courseResponse = CourseResponse.fromEntity(course);
        return new CourseDetailResponse(courseResponse, materials, List.of(), List.of(), assignments);
    }

    @Transactional
    public CourseMaterialResponse addMaterial(Long courseId, Usuario teacher,
            CourseMaterialRequest request, MultipartFile file) throws IOException {
        Course course = courseRepository.findByIdAndTeacher(courseId, teacher)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        CourseMaterial material = new CourseMaterial();
        material.setCourse(course);
        material.setType(request.getType());
        material.setTitle(request.getTitle().trim());
        material.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);

        if (request.getType() == CourseMaterialType.VIDEO_LINK) {
            if (request.getResourceUrl() == null || request.getResourceUrl().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El enlace del video es obligatorio");
            }
            material.setResourceUrl(request.getResourceUrl().trim());
            material.setFilePath(null);
        } else {
            if (file == null || file.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes adjuntar un archivo");
            }
            String storedPath = fileStorageService.store(file, "materials");
            material.setFilePath(storedPath);
            material.setResourceUrl(null);
        }

        CourseMaterial saved = courseMaterialRepository.save(material);
        return CourseMaterialResponse.fromEntity(saved, buildFileUrl(saved.getFilePath()));
    }

    @Transactional
    public void removeMaterial(Long courseId, Long materialId, Usuario teacher) {
        Course course = courseRepository.findByIdAndTeacher(courseId, teacher)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        CourseMaterial material = courseMaterialRepository.findByIdAndCourse(materialId, course)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material no encontrado"));

        if (material.getFilePath() != null) {
            fileStorageService.delete(material.getFilePath());
        }

        courseMaterialRepository.delete(material);
    }

    @Transactional
    public void deleteCourse(Long courseId, Usuario teacher) {
        Course course = courseRepository.findByIdAndTeacher(courseId, teacher)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        List<CourseMaterial> materials = courseMaterialRepository.findByCourse(course);
        materials.forEach(material -> {
            if (material.getFilePath() != null) {
                fileStorageService.delete(material.getFilePath());
            }
        });
        courseMaterialRepository.deleteAll(materials);

        List<Assignment> assignments = assignmentRepository.findByCourse(course);
        assignments.forEach(assignment -> {
            List<AssignmentSubmission> submissions = assignmentSubmissionRepository.findByAssignment(assignment);
            submissions.forEach(submission -> {
                if (submission.getFilePath() != null) {
                    fileStorageService.delete(submission.getFilePath());
                }
            });
            assignmentSubmissionRepository.deleteAll(submissions);
        });
        assignmentRepository.deleteAll(assignments);

        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByCourse(course);
        courseEnrollmentRepository.deleteAll(enrollments);

        courseRepository.delete(course);
    }

    @Transactional
    public List<StudentCourseSummaryResponse> getCoursesForStudent(Usuario student) {
        return courseEnrollmentRepository.findByStudent(student)
                .stream()
                .map(StudentCourseSummaryResponse::fromEnrollment)
                .collect(Collectors.toList());
    }

    @Transactional
    public AssignmentResponse createAssignment(Long courseId, Usuario teacher, AssignmentRequest request) {
        Course course = courseRepository.findByIdAndTeacher(courseId, teacher)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setTitle(request.getTitle().trim());
        assignment.setDescription(request.getDescription().trim());
        assignment.setDueDate(request.getDueDate());

        Assignment saved = assignmentRepository.save(assignment);
        return AssignmentResponse.fromEntity(saved, List.of());
    }

    @Transactional
    public AssignmentResponse updateAssignment(Long assignmentId, Usuario teacher, AssignmentRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada"));

        if (!assignment.getCourse().getTeacher().getId().equals(teacher.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }

        assignment.setTitle(request.getTitle().trim());
        assignment.setDescription(request.getDescription().trim());
        assignment.setDueDate(request.getDueDate());

        Assignment saved = assignmentRepository.save(assignment);
        List<AssignmentSubmissionResponse> submissions = assignmentSubmissionRepository
                .findByAssignment(saved)
                .stream()
                .map(submission -> AssignmentSubmissionResponse.fromEntity(submission, buildFileUrl(submission.getFilePath())))
                .collect(Collectors.toList());

        return AssignmentResponse.fromEntity(saved, submissions);
    }

    @Transactional
    public AssignmentSubmissionResponse submitAssignment(Long assignmentId, Usuario student, MultipartFile file)
            throws IOException {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada"));

        Course course = assignment.getCourse();
        CourseEnrollment enrollment = courseEnrollmentRepository.findByCourseAndStudent(course, student)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "No estás inscrito en este curso"));

        if (enrollment.getStatus() != EnrollmentStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Tu inscripción aún no ha sido aprobada");
        }

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes adjuntar un archivo");
        }

        String storedPath = fileStorageService.store(file, "submissions");

        AssignmentSubmission submission = assignmentSubmissionRepository
                .findByAssignmentAndStudent(assignment, student)
                .orElseGet(AssignmentSubmission::new);
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setFilePath(storedPath);
        submission.setGrade(null);
        submission.setFeedback(null);
        submission.setSubmittedAt(LocalDateTime.now());

        AssignmentSubmission saved = assignmentSubmissionRepository.save(submission);
        return AssignmentSubmissionResponse.fromEntity(saved, buildFileUrl(saved.getFilePath()));
    }

    @Transactional
    public AssignmentSubmissionResponse gradeSubmission(Long assignmentId, Long submissionId, Usuario teacher,
            GradeSubmissionRequest request) {
        AssignmentSubmission submission = assignmentSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrega no encontrada"));

        if (!submission.getAssignment().getId().equals(assignmentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La entrega no pertenece a la tarea indicada");
        }

        if (!submission.getAssignment().getCourse().getTeacher().getId().equals(teacher.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }

        if (request.getGrade() != null && (request.getGrade() < 0 || request.getGrade() > 100)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nota debe estar entre 0 y 100");
        }

        submission.setGrade(request.getGrade());
        submission.setFeedback(request.getFeedback());
        AssignmentSubmission saved = assignmentSubmissionRepository.save(submission);
        return AssignmentSubmissionResponse.fromEntity(saved, buildFileUrl(saved.getFilePath()));
    }

    @Transactional
    public JoinCourseResponse requestEnrollment(JoinCourseRequest request, Usuario student) {
        String code = request.getCode().trim().toUpperCase(Locale.ROOT);
        Course course = courseRepository.findByJoinCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        if (course.getTeacher().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes inscribirte a tu propio curso");
        }

        Optional<CourseEnrollment> existing = courseEnrollmentRepository.findByCourseAndStudent(course, student);
        if (existing.isPresent()) {
            CourseEnrollment enrollment = existing.get();
            if (enrollment.getStatus() == EnrollmentStatus.REJECTED) {
                enrollment.setStatus(EnrollmentStatus.PENDING);
                courseEnrollmentRepository.save(enrollment);
                return new JoinCourseResponse(course.getId(), enrollment.getStatus(),
                        "Solicitud reenviada al docente");
            }
            return new JoinCourseResponse(course.getId(), enrollment.getStatus(),
                    enrollment.getStatus() == EnrollmentStatus.APPROVED
                            ? "Ya estás inscrito en este curso"
                            : "Tu solicitud está pendiente");
        }

        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setStatus(EnrollmentStatus.PENDING);
        courseEnrollmentRepository.save(enrollment);
        return new JoinCourseResponse(course.getId(), EnrollmentStatus.PENDING, "Solicitud enviada correctamente");
    }

    @Transactional
    public EnrollmentResponse approveEnrollment(Long enrollmentId, Usuario teacher, boolean approve) {
        CourseEnrollment enrollment = courseEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción no encontrada"));

        if (!enrollment.getCourse().getTeacher().getId().equals(teacher.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }

        enrollment.setStatus(approve ? EnrollmentStatus.APPROVED : EnrollmentStatus.REJECTED);
        CourseEnrollment saved = courseEnrollmentRepository.save(enrollment);
        return EnrollmentResponse.fromEntity(saved);
    }

    @Transactional
    public void removeEnrollment(Long enrollmentId, Usuario teacher) {
        CourseEnrollment enrollment = courseEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción no encontrada"));

        if (!enrollment.getCourse().getTeacher().getId().equals(teacher.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }

        courseEnrollmentRepository.delete(enrollment);
    }

    private String buildFileUrl(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            return null;
        }
        String encoded = URLEncoder.encode(storedPath, StandardCharsets.UTF_8);
        return "/api/v1/files?path=" + encoded;
    }

    private String generateUniqueJoinCode() {
        String code;
        do {
            code = randomCode(6);
        } while (courseRepository.findByJoinCode(code).isPresent());
        return code;
    }

    private String randomCode(int length) {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
        }
        return sb.toString();
    }
}
