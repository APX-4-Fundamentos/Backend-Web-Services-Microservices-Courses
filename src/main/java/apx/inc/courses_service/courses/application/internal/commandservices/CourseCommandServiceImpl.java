package apx.inc.courses_service.courses.application.internal.commandservices;

import apx.inc.courses_service.courses.application.internal.services.external.AssignmentApiService;
import apx.inc.courses_service.courses.application.internal.services.external.IamApiService;
import apx.inc.courses_service.courses.domain.model.aggregates.Course;
import apx.inc.courses_service.courses.domain.model.commands.*;
import apx.inc.courses_service.courses.domain.model.valueobjects.CourseJoinCode;
import apx.inc.courses_service.courses.domain.services.CourseCommandService;
import apx.inc.courses_service.courses.infrastructure.persistence.jpa.repositories.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class CourseCommandServiceImpl implements CourseCommandService {

    private final CourseRepository courseRepository;
    private final IamApiService iamApiService;
    private final AssignmentApiService assignmentApiService;

    public CourseCommandServiceImpl(CourseRepository courseRepository, IamApiService iamApiService, AssignmentApiService assignmentApiService, AssignmentApiService assignmentApiService1) {
        this.courseRepository = courseRepository;
        this.iamApiService = iamApiService;
        this.assignmentApiService = assignmentApiService1;
    }

    @Override
    public Long handle(CreateCourseCommand createCourseCommand) { // ✅ QUITA el authHeader
        // 1. Asegurarnos que el usuario que creo el curso exista
        if (!iamApiService.userExists(createCourseCommand.teacherId())) { // ✅ LLAMADA AUTOMÁTICA
            throw new IllegalArgumentException("Teacher with ID " + createCourseCommand.teacherId() + " not found");
        }

        // 2. Verificamos que tenga el rol de teacher
        if (!iamApiService.isTeacher(createCourseCommand.teacherId())) { // ✅ LLAMADA AUTOMÁTICA
            throw new IllegalArgumentException("Only teachers can create courses");
        }

        var course = new Course(createCourseCommand);
        courseRepository.save(course);
        return course.getId();
    }

    @Override
    public void handle(DeleteCourseCommand deleteCourseCommand) {
        var course = courseRepository.findById(deleteCourseCommand.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course with ID " + deleteCourseCommand.courseId() + " not found"));

        // ✅ 1. ELIMINAR TODOS LOS ASSIGNMENTS DEL CURSO
        try {
            boolean success = assignmentApiService.deleteAssignmentsByCourseId(deleteCourseCommand.courseId());
            if (success) {
                System.out.println("✅ Assignments eliminados del curso: " + deleteCourseCommand.courseId());
            } else {
                System.out.println("⚠️ No se pudieron eliminar algunos assignments");
            }
        } catch (Exception e) {
            System.out.println("❌ Error eliminando assignments: " + e.getMessage());
        }

        // ✅ 2. NOTIFICAR A IAM (igual que antes)
        for (Long studentId : course.getStudentIds()) {
            try {
                iamApiService.removeCourseFromUser(studentId, course.getId());
                System.out.println("✅ Notificado a IAM: estudiante " + studentId + " removido del curso " + course.getId());
            } catch (Exception e) {
                System.out.println("⚠️ No se pudo notificar a IAM para estudiante " + studentId + ": " + e.getMessage());
            }
        }

        // ✅ 3. ELIMINAR EL CURSO
        courseRepository.deleteById(deleteCourseCommand.courseId());
        System.out.println("✅ Curso eliminado completamente: " + deleteCourseCommand.courseId());
    }

    @Override
    public Optional<Course> handle(JoinByJoinCodeCommand command) {
        // 1. Buscar curso por joinCode
        var course = courseRepository.findAll().stream()
                .filter(c -> c.getCourseJoinCode() != null
                        && c.getCourseJoinCode().key().equals(command.joinCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Course with joinCode " + command.joinCode() + " not found"));

        // 2. Validar expiration
        if (course.getCourseJoinCode().expiration().before(new Date())) {
            throw new IllegalArgumentException("Join code " + command.joinCode() + " has expired");
        }

        // 3. Validar que el student existe
        if (!iamApiService.userExists(command.studentId())) { // ✅ LLAMADA AUTOMÁTICA
            throw new IllegalArgumentException("User with ID " + command.studentId() + " not found");
        }

        // 4. Inscribir estudiante en CURSO LOCAL
        if (!course.hasStudent(command.studentId())) {
            course.enrollStudent(command.studentId());
            courseRepository.save(course);

            // ✅ Notificar a IAM (AUTOMÁTICO)
            iamApiService.assignCourseToUser(command.studentId(), course.getId()); // ✅ QUITA authHeader
        }

        return Optional.of(course);
    }

    @Override
    public void handle(KickStudentCommand command, Long teacherId) {
        var course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course with ID " + command.courseId() + " not found"));

        // Validar que teacher es owner
        if (!course.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException("Teacher with ID " + teacherId + " is not the owner of this course");
        }

        // Validar que student existe
        if (!iamApiService.userExists(command.studentId())) { // ✅ LLAMADA AUTOMÁTICA
            throw new IllegalArgumentException("Student with ID " + command.studentId() + " not found");
        }

        // Expulsar estudiante del CURSO LOCAL
        if (course.hasStudent(command.studentId())) {
            course.kickStudent(command.studentId());
            courseRepository.save(course);

            // ✅ Notificar a IAM (AUTOMÁTICO)
            iamApiService.removeCourseFromUser(command.studentId(), course.getId()); // ✅ QUITA authHeader
        }
    }

    @Override
    public Optional<Course> handle(ResetJoinCodeCommand resetJoinCodeCommand) {
        var course = courseRepository.findById(resetJoinCodeCommand.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course with ID " + resetJoinCodeCommand.courseId() + " not found"));

        course.resetJoinCode();
        courseRepository.save(course);

        return Optional.of(course);
    }

    @Override
    public Optional<CourseJoinCode> handle(SetJoinCodeCommand setJoinCodeCommand) {
        var course = courseRepository.findById(setJoinCodeCommand.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course with ID " + setJoinCodeCommand.courseId() + " not found"));

        // Verificar si el código ya está asignado a otro curso
        boolean isAssigned = courseRepository.findAll().stream()
                .anyMatch(c -> c.getCourseJoinCode() != null
                        && c.getCourseJoinCode().key().equals(setJoinCodeCommand.keycode()));

        if (isAssigned) {
            throw new IllegalArgumentException("Join code " + setJoinCodeCommand.keycode() + " is already assigned to another course");
        }

        var updatedCourseJoinCode = new CourseJoinCode(setJoinCodeCommand.keycode(), setJoinCodeCommand.expiration());
        var updatedCourse = course.setJoinCode(updatedCourseJoinCode);
        courseRepository.save(updatedCourse);

        return Optional.of(updatedCourseJoinCode);
    }

    @Override
    public Optional<Course> handle(UpdateCourseCommand updateCourseCommand) {
        var course = courseRepository.findById(updateCourseCommand.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course with ID " + updateCourseCommand.courseId() + " not found"));

        var updatedCourse = course.updateCourse(updateCourseCommand);
        courseRepository.save(updatedCourse);

        return Optional.of(updatedCourse);
    }
}