package apx.inc.courses_service.courses.domain.services;

import apx.inc.courses_service.courses.domain.model.aggregates.Course;
import apx.inc.courses_service.courses.domain.model.commands.*;
import apx.inc.courses_service.courses.domain.model.valueobjects.CourseJoinCode;

import java.util.Optional;

public interface CourseCommandService {

    Long handle(CreateCourseCommand createCourseCommand);

    void handle(DeleteCourseCommand deleteCourseCommand);

    Optional<Course> handle(JoinByJoinCodeCommand joinByJoinCodeCommand);

    void handle(KickStudentCommand kickStudentCommand, Long teacherId);

    Optional<Course> handle (ResetJoinCodeCommand resetJoinCodeCommand);

    Optional<CourseJoinCode> handle (SetJoinCodeCommand setJoinCodeCommand);

    Optional<Course> handle (UpdateCourseCommand updateCourseCommand);

}
