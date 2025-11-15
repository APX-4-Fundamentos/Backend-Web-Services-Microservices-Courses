package apx.inc.courses_service.courses.application.internal.services.external;

import apx.inc.courses_service.shared.infrastructure.http.dtos.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class IamApiService {

    private final RestTemplate restTemplate;

    @Value("${services.iam.url}")
    private String iamBaseUrl;

    public IamApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ✅ CORREGIDO: Obtener token del RequestContextHolder
    private String getCurrentToken() {
        try {
            var requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                String authHeader = request.getHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    return authHeader; // Ya incluye "Bearer "
                }
            }
        } catch (Exception e) {
            System.out.println("❌ No se pudo obtener el token del request: " + e.getMessage());
        }
        return null;
    }

    // ✅ Validar que un usuario existe (AUTOMÁTICO)
    public boolean userExists(Long userId) {
        try {
            String token = getCurrentToken();
            if (token == null) {
                System.out.println("❌ No hay token disponible para la request");
                return false;
            }

            String url = iamBaseUrl + "/api/v1/users/{userId}";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<UserResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, UserResponse.class, userId
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.out.println("❌ Error validando usuario en IAM: " + e.getMessage());
            return false;
        }
    }

    // ✅ Obtener información de un usuario (AUTOMÁTICO)
    public UserResponse getUserById(Long userId) {
        try {
            String token = getCurrentToken();
            if (token == null) return null;

            String url = iamBaseUrl + "/api/v1/users/{userId}";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<UserResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, UserResponse.class, userId
            );

            return response.getBody();
        } catch (Exception e) {
            System.out.println("❌ Error obteniendo usuario de IAM: " + e.getMessage());
            return null;
        }
    }

    // ✅ Validar que un usuario es TEACHER (AUTOMÁTICO)
    public boolean isTeacher(Long userId) {
        try {
            UserResponse user = getUserById(userId);
            return user != null && user.getRoles().contains("ROLE_TEACHER");
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ Asignar curso a usuario en IAM (AUTOMÁTICO)
    public boolean assignCourseToUser(Long userId, Long courseId) {
        try {
            String token = getCurrentToken();
            if (token == null) return false;

            String url = iamBaseUrl + "/api/v1/users/{userId}/courses/{courseId}";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, Void.class, userId, courseId
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo notificar a IAM: " + e.getMessage());
            return false;
        }
    }

    // ✅ Remover curso de usuario en IAM (AUTOMÁTICO)
    public boolean removeCourseFromUser(Long userId, Long courseId) {
        try {
            String token = getCurrentToken();
            if (token == null) return false;

            String url = iamBaseUrl + "/api/v1/users/{userId}/courses/{courseId}";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.DELETE, request, Void.class, userId, courseId
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo notificar a IAM: " + e.getMessage());
            return false;
        }
    }
}