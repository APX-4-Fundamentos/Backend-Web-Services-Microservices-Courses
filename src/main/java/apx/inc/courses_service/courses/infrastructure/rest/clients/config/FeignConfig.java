package apx.inc.courses_service.courses.infrastructure.rest.clients.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Value("${iam.service.api-key}")  // Opcional para autenticación service-to-service
    private String apiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Agregar headers comunes si es necesario
            if (apiKey != null && !apiKey.isBlank()) {
                requestTemplate.header("X-API-Key", apiKey);
            }
        };
    }
}