package fitloop.member.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import fitloop.member.AuthErrorCode;
import fitloop.member.dto.request.LoginRequest;
import fitloop.member.entity.UserEntity;
import fitloop.member.repository.UserRepository;
import fitloop.member.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                       UserRepository userRepository, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            LoginRequest loginRequest = objectMapper.readValue(messageBody, LoginRequest.class);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword()
            );
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            try {
                handleErrorResponse(response, AuthErrorCode.INCORRECT_CONSTRUCT_HEADER);
            } catch (IOException ioException) {
                throw new RuntimeException("Failed to write error response", ioException);
            }
            return null;
        }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) throws IOException {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.iterator().next().getAuthority();

        String accessToken = userService.createAccessToken(username, role);
        String refreshToken = userService.createRefreshToken(username, role);

        userService.saveAccessTokenToRedis(username, role, accessToken);
        userService.saveNewRefreshToken(username, refreshToken);

        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
        boolean personalInfo = userEntityOptional.map(user -> Boolean.TRUE.equals(user.getPersonalInfo())).orElse(false);

        response.addCookie(userService.createAccessCookie(accessToken));
        response.addCookie(userService.createRefreshCookie(refreshToken));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());

        response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                "message", "로그인이 성공하였습니다.",
                "personal_info", personalInfo
        )));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
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
}
