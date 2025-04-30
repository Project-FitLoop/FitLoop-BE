package fitloop.member.oauth;

import fitloop.member.entity.RefreshEntity;
import fitloop.member.entity.UserEntity;
import fitloop.member.jwt.JWTUtil;
import fitloop.member.repository.RefreshRepository;
import fitloop.member.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        UserEntity user = userRepository.findByEmail(email).orElseThrow(() ->
                new IllegalStateException("OAuth2 로그인 오류: 사용자 정보가 DB에 없음"));

        String accessToken = jwtUtil.createJwt("access", user.getUsername(), user.getRole().name(), 600000L); // 10분
        String refreshToken = jwtUtil.createJwt("refresh", user.getUsername(), user.getRole().name(), 86400000L); // 24시간

        // AccessToken Redis에 저장
        redisTemplate.opsForValue().set("ACCESS:" + accessToken, user.getUsername(), 10, TimeUnit.MINUTES);

        // RefreshToken DB에 저장
        addRefreshEntity(user.getUsername(), refreshToken, 86400000L);

        // RefreshToken을 쿠키로 전달
        response.addCookie(createCookie("refresh", refreshToken));

        // AccessToken을 헤더로 전달
        response.setHeader("access", accessToken);

        // 리다이렉트
        response.sendRedirect("http://localhost:3000/oauth2/redirect");
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); // 1일 유지
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS 환경이면 true로 바꿔야 함
        cookie.setPath("/");
        return cookie;
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }
}