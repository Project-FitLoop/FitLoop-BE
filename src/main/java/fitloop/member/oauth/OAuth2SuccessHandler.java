package fitloop.member.oauth;

import fitloop.member.entity.Membership;
import fitloop.member.entity.RefreshEntity;
import fitloop.member.entity.Role;
import fitloop.member.entity.UserEntity;
import fitloop.member.jwt.JWTUtil;
import fitloop.member.repository.RefreshRepository;
import fitloop.member.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        UserEntity user = userRepository.findByEmail(email).orElseThrow(() ->
                new IllegalStateException("OAuth2 로그인 오류: 사용자 정보가 DB에 없음"));

        String accessToken = jwtUtil.createJwt("access", user.getUsername(), user.getRole().name(), 600000L); // 10분
        String refreshToken = jwtUtil.createJwt("refresh", user.getUsername(), user.getRole().name(), 86400000L); // 24시간

        addRefreshEntity(user.getUsername(), refreshToken, 86400000L);

        response.addCookie(createCookie("refresh", refreshToken));
        response.setHeader("access", accessToken);
        response.sendRedirect("http://localhost:3000/oauth2/redirect");
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); // 1일 유지
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS가 아니라면 false로 설정
        cookie.setPath("/"); // 전체 도메인에서 쿠키 사용 가능
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