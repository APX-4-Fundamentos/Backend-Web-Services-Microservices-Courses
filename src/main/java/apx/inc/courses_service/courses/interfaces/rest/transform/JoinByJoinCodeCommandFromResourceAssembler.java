package apx.inc.courses_service.courses.interfaces.rest.transform;

import apx.inc.courses_service.courses.domain.model.commands.JoinByJoinCodeCommand;
import apx.inc.courses_service.courses.interfaces.rest.resources.JoinByJoinCodeResource;

public class JoinByJoinCodeCommandFromResourceAssembler {
    public static JoinByJoinCodeCommand toCommandFromResource(JoinByJoinCodeResource resource) {
        return new JoinByJoinCodeCommand(
                resource.studentId(),
                resource.joinCode()
        );
    }
}
