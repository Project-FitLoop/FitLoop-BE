package fitloop.product.entity.category;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import java.util.Arrays;

@Getter
public enum MiddleCategory {
    SHOES("001", "신발"),
    OUTERWEAR("002", "아우터"),
    TOP("003", "상의"),
    PANT("004", "바지"),
    DRESS("005", "원피스"),
    SKIRT("006", "스커트"),
    BAG("007", "가방"),
    FASHION_ACCESSORY("008", "패션소품");

    private final String code;
    private final String description;

    MiddleCategory(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    public static MiddleCategory fromDescription(String description) {
        return Arrays.stream(MiddleCategory.values())
                .filter(category -> category.description.equals(description))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 이름에 맞는 중카테고리는 없습니다: " + description));
    }

    public static MiddleCategory fromCode(String code) {
        return Arrays.stream(MiddleCategory.values())
                .filter(category -> category.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 코드에 맞는 중카테고리는 없습니다: " + code));
    }
}