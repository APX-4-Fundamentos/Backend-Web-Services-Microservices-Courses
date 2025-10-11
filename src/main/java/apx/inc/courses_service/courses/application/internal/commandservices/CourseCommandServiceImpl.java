package apx.inc.courses_service.courses.application.internal.commandservices;

import apx.inc.courses_service.courses.domain.model.aggregates.Course;
import apx.inc.courses_service.courses.domain.model.commands.*;
import apx.inc.courses_service.courses.domain.model.valueobjects.CourseJoinCode;
import apx.inc.courses_service.courses.domain.services.CourseCommandService;
import apx.inc.courses_service.courses.infrastructure.persistence.jpa.repositories.CourseRepository;
import apx.inc.courses_service.courses.infrastructure.rest.clients.IamServiceClient;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class CourseCommandServiceImpl implements CourseCommandService {

    private final CourseRepository courseRepository;
    private final IamServiceClient iamServiceClient;

    public CourseCommandServiceImpl(CourseRepository courseRepository, IamServiceClient iamServiceClient) {
        this.courseRepository = courseRepository;
        this.iamServiceClient = iamServiceClient;
    }

    @Override
    public Long handle(CreateCourseCommand createCourseCommand) {
        // ✅ USAR endpoints disponibles
        Boolean teacherExists = iamServiceClient.userExists(createCourseCommand.teacherId());
        if (Boolean.FALSE.equals(teacherExists)) {
            throw new IllegalArgumentException("Teacher not found");
        }

        Boolean isTeacher = iamServiceClient.userHasRole(createCourseCommand.teacherId(), "ROLE_TEACHER");
        if (Boolean.FALSE.equals(isTeacher)) {
            throw new IllegalArgumentException("Only teachers can create courses");
        }

        var course = new Course(createCourseCommand);
        courseRepository.save(course);
        return course.getId();
    }

    @Override
    public void handle(DeleteCourseCommand deleteCourseCommand) {
        if (!courseRepository.existsById(deleteCourseCommand.courseId())) {
            throw new IllegalArgumentException("Course not found");
        }

        // ✅ SOLUCIÓN: Solo eliminar curso, no limpiar usuarios (eso lo hará IAM Service)
        courseRepository.deleteById(deleteCourseCommand.courseId());
    }

    @Override
    public Optional<Course> handle(JoinByJoinCodeCommand command) {
        // 1. Buscar curso por joinCode
        var course = courseRepository.findAll().stream()
                .filter(c -> c.getCourseJoinCode() != null
                        && c.getCourseJoinCode().key().equals(command.joinCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // 2. Validar expiration
        if (course.getCourseJoinCode().expiration().before(new Date())) {
            throw new IllegalArgumentException("Join code expired");
        }

        // 3. Validar student (via IAM Service)
        Boolean studentExists = iamServiceClient.userExists(command.studentId());
        if (Boolean.FALSE.equals(studentExists)) {
            throw new IllegalArgumentException("Student not found");
        }

        Boolean isStudent = iamServiceClient.userHasRole(command.studentId(), "ROLE_STUDENT");
        if (Boolean.FALSE.equals(isStudent)) {
            throw new IllegalArgumentException("Only students can join courses");
        }

        // 4. ✅ SOLUCIÓN: Inscribir estudiante en CURSO LOCAL (no en User)
        if (!course.hasStudent(command.studentId())) {
            course.enrollStudent(command.studentId());
            courseRepository.save(course);
        }

        return Optional.of(course);
    }

    @Override
    public void handle(KickStudentCommand command, Long teacherId) {
        var course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // Validar que teacher es owner
        if (!course.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException("Not course owner");
        }

        // Validar que student existe
        Boolean studentExists = iamServiceClient.userExists(command.studentId());
        if (Boolean.FALSE.equals(studentExists)) {
            throw new IllegalArgumentException("Student not found");
        }

        // ✅ SOLUCIÓN: Expulsar estudiante del CURSO LOCAL
        if (course.hasStudent(command.studentId())) {
            course.kickStudent(command.studentId());
            courseRepository.save(course);
        }
    }

    @Override
    public Optional<Course> handle(ResetJoinCodeCommand resetJoinCodeCommand) {
        //1. Verificar que el course existe

        var optionalCourse=courseRepository.findById(resetJoinCodeCommand.courseId());

        if (optionalCourse.isEmpty()) {
            throw new IllegalArgumentException("Course with ID " + resetJoinCodeCommand.courseId() + " not found");
        }

        var course=optionalCourse.get();

        //2. Resetear el joinCode

        course.resetJoinCode();

        //3. Guardar los cambios en elr epo

        courseRepository.save(course);

        return  Optional.of(course);
    }

    @Override
    public Optional<CourseJoinCode> handle(SetJoinCodeCommand setJoinCodeCommand) {
        //1. Verificar si existe el course
        var optionalCourse=courseRepository.findById(setJoinCodeCommand.courseId());

        if (optionalCourse.isEmpty()) {
            throw new IllegalArgumentException("Course with ID " + setJoinCodeCommand.courseId() + " not found");
        }

        var course=optionalCourse.get();


        // 2. Verificar si el nuevo código ya está asignado a otro curso
        var isAssigned = courseRepository.findAll().stream().anyMatch(c->c.getCourseJoinCode().key().equals(setJoinCodeCommand.keycode()));

        if (isAssigned) {
            throw new IllegalArgumentException("Course with ID " + setJoinCodeCommand.courseId() + " is already assigned to any course");
        }

        //3.Actualizar el courseJoinCode
        var updatedCourseJoinCode=new CourseJoinCode(setJoinCodeCommand.keycode(),setJoinCodeCommand.expiration());
        var updatedCourse=course.setJoinCode(updatedCourseJoinCode);

        //4. Actualizar el repositorio
        courseRepository.save(updatedCourse);

        return Optional.of(updatedCourseJoinCode);

    }

    @Override
    public Optional<Course> handle(UpdateCourseCommand updateCourseCommand) {
        //1. Verificar si existe el course
        var optionalCourse=courseRepository.findById(updateCourseCommand.courseId());

        if (optionalCourse.isEmpty()) {
            throw new IllegalArgumentException("Course with ID " + updateCourseCommand.courseId() + " not found");
        }

        var course=optionalCourse.get();

        //2. Actualizar el course

        var updatedCourse=course.updateCourse(updateCourseCommand);

        //3. Guardamos en el repositorio
        courseRepository.save(updatedCourse);

        return  Optional.of(updatedCourse);
    }
}
