package fitloop.product.entity.category;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import java.util.Arrays;

@Getter
public enum TopCategory {
    ALL("전체"),
    MALE("남성"),
    FEMALE("여성");

    private final String description;

    TopCategory(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    public static TopCategory from(String description) {
        return Arrays.stream(TopCategory.values())
                .filter(category -> category.description.equals(description))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 이름에 맞는 대카테고리는 없습니다: " + description));
    }
}
