package apx.inc.courses_service.courses.interfaces.rest.transform;

import apx.inc.courses_service.courses.domain.model.commands.UpdateCourseCommand;
import apx.inc.courses_service.courses.interfaces.rest.resources.UpdateCourseResource;

public class UpdateCourseCommandFromResourceAssembler {
    public static UpdateCourseCommand toCommandFromResource(UpdateCourseResource resource, Long courseId) {
        return new UpdateCourseCommand(
                courseId,
                resource.title(),
                resource.imageUrl()
        );
    }
}
