package fitloop.member.entity;

import lombok.Getter;
import java.util.Arrays;

@Getter
public enum Role {
    MEMBER("회원"),
    ADMIN("관리자");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public static Role from(String name) {
        return Arrays.stream(Role.values())
                .filter(role -> role.name().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 이름에 맞는 역할(Role)이 없습니다: " + name));
    }
}