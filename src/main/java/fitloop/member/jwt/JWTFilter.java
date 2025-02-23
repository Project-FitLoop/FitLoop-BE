package fitloop.member.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import fitloop.member.AuthErrorCode;
import fitloop.member.dto.request.CustomUserDetails;
import fitloop.member.entity.Role;
import fitloop.member.entity.UserEntity;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader("access");

        if (accessToken == null) {
            handleErrorResponse(response, AuthErrorCode.NOT_EXIST_ACCESS_TOKEN);
            return;
        }

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            handleErrorResponse(response, AuthErrorCode.EXPIRED_TOKEN);
            return;
        } catch (SignatureException e) {
            handleErrorResponse(response, AuthErrorCode.FAILED_SIGNATURE_TOKEN);
            return;
        } catch (JwtException e) {
            handleErrorResponse(response, AuthErrorCode.INCORRECTLY_CONSTRUCTED_TOKEN);
            return;
        }

        String category = jwtUtil.getCategory(accessToken);
        if (!"access".equals(category)) {
            handleErrorResponse(response, AuthErrorCode.NOT_WOOHAENGSHI_TOKEN);
            return;
        }

        String username = jwtUtil.getUsername(accessToken);
        String roleString = jwtUtil.getRole(accessToken);

        Role role;
        try {
            role = Role.valueOf(roleString);
        } catch (IllegalArgumentException e) {
            handleErrorResponse(response, AuthErrorCode.INVALID_CLAIM_TYPE);
            return;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setRole(role);

        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private void handleErrorResponse(HttpServletResponse response, AuthErrorCode errorCode) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                "error", errorCode.name(),
                "message", errorCode.getMessage(),
                "status", errorCode.getStatus().value()
        )));
        response.getWriter().flush();
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/v1/login") ||
                uri.startsWith("/api/v1/register") ||
                uri.startsWith("/api/v1/auth/");
    }
}
