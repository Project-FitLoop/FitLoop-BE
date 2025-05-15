package fitloop.member.oauth;

import fitloop.member.entity.UserEntity;
import fitloop.member.repository.UserRepository;
import fitloop.member.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("OAuth2 로그인 오류: 사용자 정보가 DB에 없음"));

        String accessToken = userService.createAccessToken(user.getUsername(), user.getRole().name());
        String refreshToken = userService.createRefreshToken(user.getUsername(), user.getRole().name());

        userService.saveAccessTokenToRedis(user.getUsername(), user.getRole().name(), accessToken);
        userService.saveNewRefreshToken(user.getUsername(), refreshToken);

        response.addCookie(userService.createAccessCookie(accessToken));
        response.addCookie(userService.createRefreshCookie(refreshToken));

        response.sendRedirect("http://localhost:3000/oauth2/redirect");
    }
}
