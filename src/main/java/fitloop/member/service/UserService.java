package fitloop.member.service;

import fitloop.member.dto.request.CustomUserDetails;
import fitloop.member.dto.request.ProfileRequest;
import fitloop.member.dto.response.ProfileResponse;
import fitloop.member.entity.ProfileEntity;
import fitloop.member.entity.UserEntity;
import fitloop.member.repository.ProfileRepository;
import fitloop.member.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

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
    public ResponseEntity<ProfileResponse> createProfile(ProfileRequest profileRequest, Object principal) {
        if (!(principal instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = userRepository.findByUsername(userDetails.getUsername())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

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

        return ResponseEntity.status(HttpStatus.CREATED).body(new ProfileResponse(profile));
    }
}
