package fitloop.product.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProductConditionCategory {
    UNOPENED("미개봉"),
    LIKE_NEW("거의 새 상품"),
    USED("중고"),
    GOOD("좋음"),
    FAIR("보통"),
    POOR("나쁨");

    private final String description;

    ProductConditionCategory(String description) {
        this.description = description;
    }

    public static ProductConditionCategory from(String name) {
        return Arrays.stream(ProductConditionCategory.values())
                .filter(condition -> condition.name().equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 이름에 맞는 상품 상태가 없습니다: " + name));
    }
}
