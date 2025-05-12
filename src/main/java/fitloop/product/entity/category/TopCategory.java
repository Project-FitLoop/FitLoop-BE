package fitloop.product.entity.category;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import java.util.Arrays;

@Getter
public enum TopCategory {
    ALL("A", "공용"),
    MALE("M", "남성"),
    FEMALE("F", "여성");

    private final String gender;
    private final String description;

    TopCategory(String gender, String description) {
        this.gender = gender;
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    public static TopCategory fromDescription(String description) {
        return Arrays.stream(TopCategory.values())
                .filter(category -> category.description.equals(description))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 성별에 맞는 대카테고리는 없습니다: " + description));
    }

    public static TopCategory fromGender(String gender) {
        return Arrays.stream(TopCategory.values())
                .filter(category -> category.gender.equals(gender))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 성별에 맞는 대카테고리는 없습니다: " + gender));
    }
}
