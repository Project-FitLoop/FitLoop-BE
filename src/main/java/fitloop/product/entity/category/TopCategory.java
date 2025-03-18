package fitloop.product.entity.category;

import lombok.Getter;
import java.util.Arrays;

@Getter
public enum TopCategory {
    ALL("전체"),
    MEN("남성"),
    WOMEN("여성");

    private final String description;

    TopCategory(String description) {
        this.description = description;
    }

    public static TopCategory from(String name) {
        return Arrays.stream(TopCategory.values())
                .filter(category -> category.name().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 이름에 맞는 대카테고리는 없습니다: " + name));
    }

}
