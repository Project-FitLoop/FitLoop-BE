package fitloop.member.controller;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class OAuth2LoginController {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2LoginController(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    // 프론트엔드에서 소셜 로그인 요청 시 OAuth2 로그인 URL 반환
    @GetMapping("/{provider}")
    public Map<String, String> getOAuth2LoginUrl(@PathVariable String provider) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(provider);

        if (clientRegistration == null) {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인: " + provider);
        }

        // OAuth2 로그인 URL 생성
        String authUrl = "http://localhost:8080/oauth2/authorization/" + provider;

        Map<String, String> response = new HashMap<>();
        response.put("authUrl", authUrl);
        return response;
    }
}
