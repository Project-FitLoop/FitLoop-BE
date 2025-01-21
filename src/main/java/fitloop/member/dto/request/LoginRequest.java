package fitloop.member.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    //유효성
    private String username;

    //유효성
    private String password;
}
