package apx.inc.courses_service.courses.infrastructure.authorization.sfs.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Value("${jwt.secret:WriteHereYourSecretStringForTokenSigningCredentials}")
    private String jwtSecret;

    public Long getAuthenticatedUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        System.out.println("🔐 Authentication Principal Type: " + authentication.getPrincipal().getClass().getName());

        // ✅ OPCIÓN A - Spring Security ya validó el JWT
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            System.out.println("✅ JWT Claims: " + jwt.getClaims());
            return extractUserIdFromJwt(jwt);
        }

        throw new RuntimeException("Cannot extract user ID from authentication. Principal type: " +
                authentication.getPrincipal().getClass().getName());
    }

    private Long extractUserIdFromJwt(Jwt jwt) {
        // 1. Intentar con user_id claim
        Object userIdObj = jwt.getClaims().get("user_id");
        Long userId = (userIdObj instanceof Number) ? ((Number) userIdObj).longValue() : null;
        if (userId != null) {
            System.out.println("✅ User ID from 'user_id' claim: " + userId);
            return userId;
        }

        // 2. Intentar con sub (subject) claim
        String subject = jwt.getSubject();
        System.out.println("🔍 JWT Subject: " + subject);
        if (subject != null && subject.matches("\\d+")) {
            Long userIdFromSub = Long.parseLong(subject);
            System.out.println("✅ User ID from 'sub' claim: " + userIdFromSub);
            return userIdFromSub;
        }

        // 3. Fallback: username como número
        String username = jwt.getClaimAsString("username");
        System.out.println("🔍 JWT Username: " + username);
        if (username != null && username.matches("\\d+")) {
            Long userIdFromUsername = Long.parseLong(username);
            System.out.println("✅ User ID from 'username' claim: " + userIdFromUsername);
            return userIdFromUsername;
        }

        throw new RuntimeException("User ID not found in JWT token. Available claims: " + jwt.getClaims().keySet());
    }
}