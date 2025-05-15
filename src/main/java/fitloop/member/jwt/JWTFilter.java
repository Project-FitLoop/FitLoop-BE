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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JWTFilter(JWTUtil jwtUtil, RedisTemplate<String, String> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader("access");

        // 토큰이 없으면 그냥 다음 필터로 넘김
        if (accessToken != null) {
            try {
                jwtUtil.isExpired(accessToken);

                String username = jwtUtil.getUsername(accessToken);
                String redisKey = "auth:access:" + username;
                String redisValue = redisTemplate.opsForValue().get(redisKey);

                if (redisValue == null) {
                    setErrorResponse(response, AuthErrorCode.EXPIRED_TOKEN);
                    return;
                }

                // value가 JSON 형태일 경우 token 일치 여부 확인
                Map<String, String> valueMap = objectMapper.readValue(redisValue, Map.class);
                if (!accessToken.equals(valueMap.get("token"))) {
                    setErrorResponse(response, AuthErrorCode.EXPIRED_TOKEN);
                    return;
                }

                String category = jwtUtil.getCategory(accessToken);
                if (!"access".equals(category)) {
                    setErrorResponse(response, AuthErrorCode.NOT_WOOHAENGSHI_TOKEN);
                    return;
                }

                String roleString = valueMap.get("role");
                Role role = Role.valueOf(roleString);

                UserEntity userEntity = new UserEntity();
                userEntity.setUsername(username);
                userEntity.setRole(role);

                CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (ExpiredJwtException e) {
                setErrorResponse(response, AuthErrorCode.EXPIRED_TOKEN);
                return;
            } catch (SignatureException e) {
                setErrorResponse(response, AuthErrorCode.FAILED_SIGNATURE_TOKEN);
                return;
            } catch (JwtException | IllegalArgumentException e) {
                setErrorResponse(response, AuthErrorCode.INCORRECTLY_CONSTRUCTED_TOKEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setErrorResponse(HttpServletResponse response, AuthErrorCode errorCode) throws IOException {
        // 인증 실패 시 SecurityContext를 비우고 에러 응답 전송
        SecurityContextHolder.clearContext();
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
}
