package fitloop.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRequest {
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    @NotBlank(message = "성별은 필수 입력 값입니다.")
    private String gender;

    @NotBlank(message = "연령대는 필수 입력 값입니다.")
    private String ageRange;

    @NotBlank(message = "키는 필수 입력 값입니다.")
    private Double height;

    @NotBlank(message = "몸무게는 필수 입력 값입니다.")
    private Double weight;
}