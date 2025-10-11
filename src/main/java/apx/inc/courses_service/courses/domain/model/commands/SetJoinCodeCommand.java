package apx.inc.courses_service.courses.domain.model.commands;

import java.util.Date;

public record SetJoinCodeCommand(
        Long courseId,
        String keycode,
        Date expiration
) {

}
