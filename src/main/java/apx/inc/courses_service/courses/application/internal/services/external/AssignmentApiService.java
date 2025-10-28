package apx.inc.courses_service.courses.application.internal.services.external;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AssignmentApiService {

    private final RestTemplate restTemplate;

    @Value("${services.assignments.url:http://localhost:8083}")
    private String assignmentsBaseUrl;

    public AssignmentApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ✅ MISMO PATRÓN: Obtener token del RequestContextHolder
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

    // ✅ ELIMINAR todos los assignments de un curso
    public boolean deleteAssignmentsByCourseId(Long courseId) {
        try {
            String token = getCurrentToken();
            if (token == null) {
                System.out.println("❌ No hay token disponible para llamar al servicio de assignments");
                return false;
            }

            String url = assignmentsBaseUrl + "/api/v1/assignments/course/" + courseId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.DELETE, request, Void.class
            );

            boolean success = response.getStatusCode().is2xxSuccessful();
            if (success) {
                System.out.println("✅ Assignments del curso " + courseId + " eliminados exitosamente");
            } else {
                System.out.println("⚠️ No se pudieron eliminar assignments: " + response.getStatusCode());
            }

            return success;
        } catch (Exception e) {
            System.out.println("❌ Error eliminando assignments del curso " + courseId + ": " + e.getMessage());
            return false;
        }
    }
}