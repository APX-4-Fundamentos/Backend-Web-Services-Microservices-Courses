package apx.inc.courses_service.courses.interfaces.rest.resources;

public record CourseResource(
        Long courseId,
        Long teacherId,
        String title,
        String imageUrl,
        String key  // ← Join code como resource
) {
    public CourseResource {
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException(" courseId can not be null, zero or negative ");
        }
        if (teacherId == null || teacherId <= 0) {
            throw new IllegalArgumentException(" teacherId can not be null, zero or negative ");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title can not be null or empty ");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException(" imageUrl can not be null or empty ");
        }
    }
}
