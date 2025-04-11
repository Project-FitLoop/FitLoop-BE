package fitloop.config;

import fitloop.common.exception.errorcode.CommonErrorCode;
import fitloop.member.jwt.CustomLogoutFilter;
import fitloop.member.jwt.JWTFilter;
import fitloop.member.jwt.JWTUtil;
import fitloop.member.jwt.LoginFilter;
import fitloop.member.oauth.OAuth2SuccessHandler;
import fitloop.member.repository.RefreshRepository;
import fitloop.member.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final UserRepository userRepository;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil,
                          RefreshRepository refreshRepository, OAuth2SuccessHandler oAuth2SuccessHandler, UserRepository userRepository) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.userRepository = userRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
            configuration.setAllowedMethods(Collections.singletonList("*"));
            configuration.setAllowCredentials(true);
            configuration.setAllowedHeaders(Collections.singletonList("*"));
            configuration.setMaxAge(3600L);
            configuration.setExposedHeaders(List.of("Set-Cookie", "access"));
            return configuration;
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationManager();

        LoginFilter loginFilter = new LoginFilter(authenticationManager, jwtUtil, refreshRepository, userRepository);
        loginFilter.setFilterProcessesUrl("/api/v1/login");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 공개 URL은 여기서 지정 (JWTFilter 내부에 URL 체크 로직을 제거)
                        .requestMatchers("/api/v1/google", "/api/v1/login", "/api/v1/register",
                                "/api/v1/reissue", "/api/v1/login/oauth2/code/google",
                                "/api/v1/oauth2/authorization/google", "/api/v1/auth/**", "/api/v1/products/recent"
                        ).permitAll()
                        .requestMatchers("/api/v1/products/register", "/api/v1/upload", "/api/v1/users/profile").authenticated()
                        .requestMatchers("/api/v1/user").hasAuthority("MEMBER")
                        .requestMatchers("/api/v1/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2SuccessHandler))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                );

        // JWTFilter는 헤더에 토큰이 있을 때만 인증 검증을 수행하며, 공개 URL은 SecurityConfig에서 처리됨
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        http.addFilterAt(loginFilter, LoginFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            String requestURI = request.getRequestURI();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            if (!isRegisteredAPI(requestURI)) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of(
                        "error", CommonErrorCode.RESOURCE_NOT_FOUND.name(),
                        "message", CommonErrorCode.RESOURCE_NOT_FOUND.getMessage(requestURI)
                )));
            } else {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of(
                        "error", CommonErrorCode.UNAUTHORIZED.name(),
                        "message", CommonErrorCode.UNAUTHORIZED.getMessage()
                )));
            }
            response.getWriter().flush();
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of(
                    "error", CommonErrorCode.FORBIDDEN.name(),
                    "message", CommonErrorCode.FORBIDDEN.getMessage()
            )));
            response.getWriter().flush();
        };
    }

    private boolean isRegisteredAPI(String requestURI) {
        return List.of(
                "/api/v1/google", "/api/v1/login", "/api/v1/register",
                "/api/v1/reissue", "/api/v1/auth/", "/api/v1/user",
                "/api/v1/admin", "/api/v1/users/profile", "/api/v1/products/register",
                "/api/v1/upload"
        ).stream().anyMatch(requestURI::startsWith);
    }
}
