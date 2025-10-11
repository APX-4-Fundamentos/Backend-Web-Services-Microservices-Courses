package apx.inc.courses_service.courses.interfaces.rest.transform;

import apx.inc.courses_service.courses.domain.model.aggregates.Course;
import apx.inc.courses_service.courses.interfaces.rest.resources.CourseResource;

public class CourseResourceFromEntityAssembler {
    public static CourseResource toResourceFromEntity(Course entity) {
        return new CourseResource(
                entity.getId(),
                entity.getTeacherId(),
                entity.getTitle(),
                entity.getImageUrl(),
                entity.getCourseJoinCode() != null ? entity.getCourseJoinCode().key() : null  // ✅ Manejar null
        );
    }
}
