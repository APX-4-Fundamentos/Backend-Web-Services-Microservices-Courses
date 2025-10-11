package apx.inc.courses_service.courses.interfaces.rest.transform;

import apx.inc.courses_service.courses.domain.model.commands.SetJoinCodeCommand;
import apx.inc.courses_service.courses.interfaces.rest.resources.SetJoinCodeResource;

public class SetJoinCodeCommandFromResourceAssembler {
    public static SetJoinCodeCommand toCommandFromResource(Long courseId,SetJoinCodeResource resource) {
        return  new SetJoinCodeCommand(
                courseId,
                resource.keycode(),
                resource.expiration()
        );
    }
}
