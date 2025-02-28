package fitloop.member.oauth;

import fitloop.member.dto.request.CustomUserDetails;
import fitloop.member.entity.Membership;
import fitloop.member.entity.Role;
import fitloop.member.entity.UserEntity;
import fitloop.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = null;

        if ("google".equals(provider)) {
            oAuth2UserInfo = new GoogleUserDetails(oAuth2User.getAttributes());
        }

        if (oAuth2UserInfo == null) {
            throw new OAuth2AuthenticationException("OAuth2UserInfo is null");
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();

        //사용자 조회 후 없으면 저장
        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserEntity newUser = UserEntity.builder()
                            .username("유저") //기본값으로 유저 등록 나중에 바꿔야 함
                            .email(email)
                            .provider(provider)
                            .providerId(providerId)
                            .role(Role.MEMBER)
                            .membership(Membership.SEED) // 기본 membership 설정
                            .password(null) // OAuth 로그인은 비밀번호 X
                            .phoneNumber(null) // 기본 전화번호 없음
                            .personalInfo(false) // 개인정보 동의 기본값
                            .build();
                    return userRepository.save(newUser);
                });

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }
}