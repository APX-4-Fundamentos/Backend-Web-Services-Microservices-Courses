package apx.inc.courses_service.courses.interfaces.rest.transform;

import apx.inc.courses_service.courses.domain.model.aggregates.Course;
import apx.inc.courses_service.courses.interfaces.rest.resources.CourseResource;

import java.util.Set;

public class CourseResourceFromEntityAssembler {
    public static CourseResource toResourceFromEntity(Course entity) {
        return new CourseResource(
                entity.getId(),
                entity.getTeacherId(),
                entity.getTitle(),
                entity.getImageUrl(),
                entity.getCourseJoinCode() != null ? entity.getCourseJoinCode().key() : null,
                entity.getStudentIds() != null ? Set.copyOf(entity.getStudentIds()) : Set.of()
        );
    }
}