package apx.inc.courses_service.courses.interfaces.rest.resources;

import java.util.Date;

public record CourseJoinCodeResource(
        String key,
        Date expiration
) {
    public CourseJoinCodeResource{
        if (key==null || key.isBlank()) {
            throw new IllegalArgumentException("key can not be null or empty ");
        }
        if (expiration==null || expiration.before(new Date())) {
            throw new IllegalArgumentException("expiration can not be null or be in the past ");
        }
    }
}
