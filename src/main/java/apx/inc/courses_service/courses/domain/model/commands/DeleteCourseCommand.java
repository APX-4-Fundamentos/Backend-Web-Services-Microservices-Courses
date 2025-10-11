package apx.inc.courses_service.courses.domain.model.commands;

public record DeleteCourseCommand(
        Long courseId
) {
    public DeleteCourseCommand{
        if (courseId==null||courseId<=0){
            throw new IllegalArgumentException("El id del course no puede ser negativo");
        }
    }
}
