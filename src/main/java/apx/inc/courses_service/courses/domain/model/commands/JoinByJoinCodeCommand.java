package apx.inc.courses_service.courses.domain.model.commands;

public record JoinByJoinCodeCommand(
        Long studentId,
        String joinCode
) {
    public JoinByJoinCodeCommand{
        if (studentId==null||studentId<=0){
            throw new IllegalArgumentException("El id del course no puede ser negativo");
        }
        if (joinCode==null||joinCode.isBlank()){
            throw new IllegalArgumentException("El code del course  no puede ser nulo o vacio");
        }
    }
}
