package apx.inc.courses_service.courses.interfaces.rest.transform;

import apx.inc.courses_service.courses.domain.model.valueobjects.CourseJoinCode;
import apx.inc.courses_service.courses.interfaces.rest.resources.CourseJoinCodeResource;

public class CourseJoinCodeResourceFromEntityAssembler {
    public static CourseJoinCodeResource toResourceFromEntity(CourseJoinCode entity) {
        return new CourseJoinCodeResource(
                entity.key(),
                entity.expiration()
        );
    }
}
