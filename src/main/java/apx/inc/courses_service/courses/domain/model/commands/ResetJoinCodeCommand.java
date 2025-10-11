package apx.inc.courses_service.courses.domain.model.commands;

public record ResetJoinCodeCommand(
        Long courseId
) {
    public ResetJoinCodeCommand{
        if (courseId==null||courseId<=0){
            throw new IllegalArgumentException("El id del course no puede ser negativo");
        }
    }
}
