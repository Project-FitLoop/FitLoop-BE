package fitloop.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String username;   // 사용자명
    private String token;      // 액세스 토큰 (JWT)
    private boolean personalInfo; // 개인정보 입력 여부 (0이면 입력 필요, 1이면 메인 페이지로 이동)
    private String message;
}
