package fitloop.member.service;

import fitloop.member.dto.request.CustomUserDetails;
import fitloop.member.dto.request.ProfileRequest;
import fitloop.member.dto.response.ProfileResponse;
import fitloop.member.entity.ProfileEntity;
import fitloop.member.entity.UserEntity;
import fitloop.member.jwt.JWTUtil;
import fitloop.member.repository.ProfileRepository;
import fitloop.member.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final JWTUtil jwtUtil;

    public ResponseEntity<?> getUserInfo(Object principal) {
        if (!(principal instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername(),
                "roles", userDetails.getAuthorities()
        ));
    }

    @Transactional
    public ResponseEntity<?> createProfile(ProfileRequest profileRequest, Object principal, String accessToken) {
        if (!(principal instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = jwtUtil.getUsername(accessToken);

        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "사용자를 찾을 수 없습니다."));
        }

        UserEntity user = optionalUser.get();

        user.setPersonalInfo(true);
        userRepository.save(user);

        // 새로운 프로필 엔티티 생성
        ProfileEntity profile = ProfileEntity.builder()
                .userId(user)
                .nickname(profileRequest.getNickname())
                .gender(profileRequest.getGender())
                .ageRange(profileRequest.getAgeRange())
                .height(profileRequest.getHeight())
                .weight(profileRequest.getWeight())
                .build();

        profileRepository.save(profile);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "프로필 작성 성공"));
    }
}
