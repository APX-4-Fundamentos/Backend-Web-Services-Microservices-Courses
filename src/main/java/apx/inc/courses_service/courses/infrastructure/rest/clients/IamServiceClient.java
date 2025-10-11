package apx.inc.courses_service.courses.infrastructure.rest.clients;

import apx.inc.courses_service.courses.infrastructure.rest.clients.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "iam-service",
        url = "${iam.service.url:http://localhost:8081}",
        configuration = FeignConfig.class
)
public interface IamServiceClient {

    // Validar que usuario existe
    @GetMapping("/api/v1/users/{userId}/exists")
    Boolean userExists(@PathVariable Long userId);

    // Validar que usuario tiene rol específico
    @GetMapping("/api/v1/users/{userId}/role/{role}")
    Boolean userHasRole(@PathVariable Long userId, @PathVariable String role);

    // Obtener información básica del usuario
    @GetMapping("/api/v1/users/{userId}")
    UserResponse getUser(@PathVariable Long userId);

    // Endpoint para inscribir usuario en curso (si lo implementamos en IAM)
    @PostMapping("/api/v1/users/{userId}/courses/{courseId}")
    void enrollUserInCourse(@PathVariable Long userId, @PathVariable Long courseId);

    // DTO para respuesta de usuario
    record UserResponse(Long id, String userName, java.util.List<String> roles) {}
}