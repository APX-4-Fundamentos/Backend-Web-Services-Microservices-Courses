package apx.inc.courses_service.courses.infrastructure.authorization.sfs.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class AuthenticationService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public Long getAuthenticatedUserId(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Token not provided or invalid format");
        }

        // Quitar el prefijo Bearer
        String jwtToken = token.substring(7);

        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();

            // Intentar obtener el user_id
            Object userIdObj = claims.get("user_id");
            if (userIdObj instanceof Number num) {
                return num.longValue();
            }

            // Intentar con "sub"
            String sub = claims.getSubject();
            if (sub != null && sub.matches("\\d+")) {
                return Long.parseLong(sub);
            }

            // Intentar con "username"
            Object username = claims.get("username");
            if (username != null && username.toString().matches("\\d+")) {
                return Long.parseLong(username.toString());
            }

            throw new RuntimeException("User ID not found in token claims");

        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage(), e);
        }
    }
}
