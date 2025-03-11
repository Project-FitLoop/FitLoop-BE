package fitloop.member.dto.response;

import fitloop.member.entity.ProfileEntity;
import lombok.Getter;

@Getter
public class ProfileResponse {
    private final String nickname;
    private final String gender;
    private final String ageRange;
    private final Double height;
    private final Double weight;

    public ProfileResponse(ProfileEntity profile) {
        this.nickname = profile.getNickname();
        this.gender = profile.getGender();
        this.ageRange = profile.getAgeRange();
        this.height = profile.getHeight();
        this.weight = profile.getWeight();
    }
}
