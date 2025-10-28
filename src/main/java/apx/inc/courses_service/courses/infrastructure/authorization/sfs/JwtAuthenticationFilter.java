package apx.inc.courses_service.courses.infrastructure.authorization.sfs;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        // DEBUG
        System.out.println("🎯 URL solicitada: " + request.getRequestURI());
        System.out.println("🔐 Token recibido: " + (token != null ? "SÍ" : "NO"));

        if (token != null && validateToken(token)) {
            String username = getUsernameFromToken(token);
            List<GrantedAuthority> authorities = getAuthoritiesFromToken(token);

            System.out.println("✅ Usuario autenticado: " + username);
            System.out.println("🎭 Roles: " + authorities);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            System.out.println("❌ Token inválido o ausente");
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error validando token: " + e.getMessage());
            return false;
        }
    }

    private String getUsernameFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

            // Primero intentar con el subject
            String subject = claims.getSubject();
            if (subject != null && !subject.isEmpty()) {
                return subject;
            }

            // Si no, intentar con el claim "username"
            Object usernameClaim = claims.get("username");
            if (usernameClaim != null) {
                return usernameClaim.toString();
            }

            return "unknown";
        } catch (Exception e) {
            return "invalid";
        }
    }

    private List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

            // Extraer roles del token
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            if (roles == null) {
                roles = claims.get("authorities", List.class);
            }

            if (roles != null) {
                return roles.stream()
                        .map(role -> {
                            if (!role.startsWith("ROLE_")) {
                                return "ROLE_" + role;
                            }
                            return role;
                        })
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }

            return List.of(new SimpleGrantedAuthority("ROLE_USER"));

        } catch (Exception e) {
            return List.of();
        }
    }
}