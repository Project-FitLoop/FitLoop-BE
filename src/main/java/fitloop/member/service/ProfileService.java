package fitloop.member.service;

import fitloop.member.entity.ProfileEntity;
import fitloop.member.entity.UserEntity;
import fitloop.member.repository.ProfileRepository;
import fitloop.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileEntity createProfile(long userId, String nickname, String gender, String ageRange, Double height, Double weight) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ProfileEntity profile = ProfileEntity.builder()
                .userId(user)
                .nickname(nickname)
                .gender(gender)
                .ageRange(ageRange)
                .height(height)
                .weight(weight)
                .build();

        return profileRepository.save(profile);
    }
}