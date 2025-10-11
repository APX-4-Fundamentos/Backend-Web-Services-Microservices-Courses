package apx.inc.courses_service.courses.interfaces.rest.transform;

import apx.inc.courses_service.courses.domain.model.commands.CreateCourseCommand;
import apx.inc.courses_service.courses.interfaces.rest.resources.CreateCourseResource;

public class CreateCourseCommandFromResourceAssembler {
    public static CreateCourseCommand toCommandFromResource(CreateCourseResource resource,Long teacherId) {
        return new CreateCourseCommand(
                resource.title(),
                teacherId
        );
    }
}
