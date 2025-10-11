package apx.inc.courses_service.courses.domain.model.commands;

public record UpdateCourseCommand(
        Long courseId,
        String title,
        String imageUrl
) {
}
