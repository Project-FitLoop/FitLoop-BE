package fitloop.member.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Membership {
    SEED("씨앗", 1),
    SPROUT("새싹", 2),
    LEAF("잎새", 3),
    BRANCH("가지", 4),
    FRUIT("열매", 5),
    TREE("나무", 6),
    NONE("해당없음", 0);

    private final String name;
    private final int value;

    Membership(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static Membership from(int value) {
        return Arrays.stream(Membership.values())
                .filter(membership -> membership.getValue() == value)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 값에 맞는 멤버십 등급이 없습니다: " + value));
    }
}
