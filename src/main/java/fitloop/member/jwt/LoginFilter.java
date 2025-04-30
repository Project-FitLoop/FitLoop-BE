package fitloop.member.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import fitloop.member.AuthErrorCode;
import fitloop.member.dto.request.LoginRequest;
import fitloop.member.entity.RefreshEntity;
import fitloop.member.entity.UserEntity;
import fitloop.member.repository.RefreshRepository;
import fitloop.member.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환용

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                       RefreshRepository refreshRepository, UserRepository userRepository,
                       RedisTemplate<String, String> redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LoginRequest loginRequest;

        try {
            String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            loginRequest = objectMapper.readValue(messageBody, LoginRequest.class);
        } catch (IOException e) {
            try {
                handleErrorResponse(response, AuthErrorCode.INCORRECT_CONSTRUCT_HEADER);
                return null;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // JWT 생성
        String accessToken = jwtUtil.createJwt("access", username, role, 600000L);
        String refreshToken = jwtUtil.createJwt("refresh", username, role, 86400000L);

        // AccessToken Redis 저장 (TTL 10분)
        redisTemplate.opsForValue().set("ACCESS:" + accessToken, username, 10, TimeUnit.MINUTES);

        // RefreshToken DB 저장
        saveRefreshToken(username, refreshToken, 86400000L);

        // 유저 정보 조회
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
        boolean personalInfo = userEntityOptional.map(user -> Boolean.TRUE.equals(user.getPersonalInfo())).orElse(false);

        response.setHeader("access", accessToken);
        response.addCookie(createCookie("refresh", refreshToken));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());

        response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                "message", "로그인이 성공하였습니다.",
                "personal_info", personalInfo
        )));
        response.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        if (failed instanceof BadCredentialsException) {
            handleErrorResponse(response, AuthErrorCode.FAIL_TO_SIGN_IN);
        } else {
            handleErrorResponse(response, AuthErrorCode.INSUFFICIENT_PERMISSIONS);
        }
    }

    private void handleErrorResponse(HttpServletResponse response, AuthErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                "error", errorCode.name(),
                "message", errorCode.getMessage()
        )));
        response.getWriter().flush();
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1일
        return cookie;
    }

    private void saveRefreshToken(String username, String refreshToken, Long expirationMs) {
        Date expirationDate = new Date(System.currentTimeMillis() + expirationMs);
        RefreshEntity refreshEntity = new RefreshEntity(username, refreshToken, expirationDate.toString());
        refreshRepository.save(refreshEntity);
    }
}
