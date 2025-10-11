package apx.inc.courses_service.courses.interfaces.rest.resources;

public record JoinByJoinCodeResource(
        Long studentId,
        String joinCode
) {
    public JoinByJoinCodeResource{
        if (studentId==null||studentId<=0){
            throw new IllegalArgumentException("El id del course no puede ser negativo");
        }
        if (joinCode==null||joinCode.isBlank()){
            throw new IllegalArgumentException("El code del course  no puede ser nulo o vacio");
        }
    }
}
