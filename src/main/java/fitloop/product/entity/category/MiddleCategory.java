package fitloop.product.entity.category;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import java.util.Arrays;

@Getter
public enum MiddleCategory {
    SHOES("신발"),
    OUTERWEAR("아우터"),
    TOP("상의"),
    PANT("바지"),
    DRESS("원피스"),
    SKIRT("스커트"),
    BAG("가방"),
    FASHION_ACCESSORY("패션소품");

    private final String description;

    MiddleCategory(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    public static MiddleCategory from(String description) {
        return Arrays.stream(MiddleCategory.values())
                .filter(category -> category.description.equals(description))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 이름에 맞는 중카테고리는 없습니다: " + description));
    }
}