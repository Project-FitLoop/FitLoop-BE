package fitloop.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull(message = "키는 필수 입력 값입니다.")
    private Double height;

    @NotNull(message = "몸무게는 필수 입력 값입니다.")
    private Double weight;
}
