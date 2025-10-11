package apx.inc.courses_service.courses.domain.model.commands;

public record KickStudentCommand(
        Long studentId,
        Long courseId
) {
    public KickStudentCommand{
        if (studentId==null||studentId<=0){
            throw new IllegalArgumentException("El id del student no puede ser negativo");
        }
        if (courseId==null||courseId<=0){
            throw new IllegalArgumentException("El id del course no puede ser negativo");
        }
    }
}
