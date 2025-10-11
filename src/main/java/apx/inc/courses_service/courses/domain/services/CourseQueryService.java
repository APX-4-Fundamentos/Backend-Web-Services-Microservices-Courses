package apx.inc.courses_service.courses.domain.services;

import apx.inc.courses_service.courses.domain.model.aggregates.Course;
import apx.inc.courses_service.courses.domain.model.queries.*;
import apx.inc.courses_service.courses.domain.model.valueobjects.CourseJoinCode;

import java.util.List;
import java.util.Optional;

public interface CourseQueryService {
    List<Course> handle(GetAllCoursesQuery getAllCoursesQuery);

    Optional<Course> handle(GetCourseByIdQuery getCourseByIdQuery);

    Optional<CourseJoinCode> handle(GetCourseJoinCodeById getCourseJoinCodeByIdQuery);

    List<Course> handle(GetCoursesByStudentIdQuery getCoursesByStudentIdQuery);

    List<Course> handle(GetCoursesByTeacherIdQuery getCoursesByTeacherIdQuery);

}
