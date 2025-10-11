package apx.inc.courses_service.courses.interfaces.rest.resources;

import java.util.Date;

public record SetJoinCodeResource(
        String keycode,
        Date expiration
) {
    public SetJoinCodeResource{
        if (keycode == null||keycode.isBlank()) {
            throw new IllegalArgumentException(" keycode cannot be null or empty ");

        }
        if (expiration == null||expiration.before(new Date())) {
            throw new IllegalArgumentException("expiration cannot be null or less than zero");
        }
    }
}
