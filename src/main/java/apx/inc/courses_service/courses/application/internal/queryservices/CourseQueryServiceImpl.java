package apx.inc.courses_service.courses.application.internal.queryservices;

import apx.inc.courses_service.courses.domain.model.aggregates.Course;
import apx.inc.courses_service.courses.domain.model.queries.*;
import apx.inc.courses_service.courses.domain.model.valueobjects.CourseJoinCode;
import apx.inc.courses_service.courses.domain.services.CourseQueryService;
import apx.inc.courses_service.courses.infrastructure.persistence.jpa.repositories.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseQueryServiceImpl implements CourseQueryService {

    private final CourseRepository courseRepository;


    public CourseQueryServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;

    }

    @Override
    public List<Course> handle(GetAllCoursesQuery getAllCoursesQuery) {
        return courseRepository.findAll();
    }

    @Override
    public Optional<Course> handle(GetCourseByIdQuery query) {
        return courseRepository.findById(query.courseId());
    }

    @Override
    public Optional<CourseJoinCode> handle(GetCourseJoinCodeById getCourseJoinCodeByIdQuery) {
        var optionalCourse= courseRepository.findById(getCourseJoinCodeByIdQuery.courseId());

        if(optionalCourse.isEmpty()){
            throw new IllegalArgumentException("Course not found");
        }

        var courseJoinCode = optionalCourse.get().getCourseJoinCode();

        return Optional.of(courseJoinCode);
    }

    @Override
    public List<Course> handle(GetCoursesByStudentIdQuery query) {
//        // 1. Validar student existe y es STUDENT
//        Boolean studentExists = iamServiceClient.userExists(query.studentId());
//        if (Boolean.FALSE.equals(studentExists)) {
//            throw new IllegalArgumentException("Student not found");
//        }
//
//        Boolean isStudent = iamServiceClient.userHasRole(query.studentId(), "ROLE_STUDENT");
//        if (Boolean.FALSE.equals(isStudent)) {
//            throw new IllegalArgumentException("User is not a student");
//        }

        // 2. ✅ SOLUCIÓN: Buscar en cursos LOCALES donde el student esté inscrito
        return courseRepository.findAll().stream()
                .filter(course -> course.hasStudent(query.studentId()))
                .toList();
    }

    @Override
    public List<Course> handle(GetCoursesByTeacherIdQuery query) {
//        // Validar teacher existe
//        Boolean teacherExists = iamServiceClient.userExists(query.teacherId());
//        if (Boolean.FALSE.equals(teacherExists)) {
//            throw new IllegalArgumentException("Teacher not found");
//        }

        return courseRepository.findByTeacherId(query.teacherId());
    }
}
