package fitloop.product.dto.request;

import fitloop.product.entity.ProductConditionCategory;
import fitloop.product.entity.category.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductRegisterRequest {
    @NotBlank(message = "상품명은 필수 입력값입니다.")
    private String productName;

    @NotBlank(message = "카테고리는 필수 입력값입니다.")
    private String category;

    @NotBlank(message = "서브 카테고리는 필수 입력값입니다.")
    private String subCategory;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    @NotNull(message = "무료 여부는 필수 입력값입니다.")
    private boolean isFree;

    @NotNull(message = "배송비 포함 여부는 필수 입력값입니다.")
    private boolean inCludeShipping;

    @NotEmpty(message = "상품 이미지는 최소 1개 이상 필요합니다.")
    private List<String> images;

    @NotBlank(message = "상품 상태는 필수 입력값입니다.")
    private String productCondition;

    @Size(max = 500, message = "상품 설명은 500자 이내여야 합니다.")
    private String productDescription;

    @NotBlank(message = "성별 선택은 필수 입력값입니다.")
    private String gender;

    @NotNull(message = "상품 태그명은 최소 1개 이상 필요합니다.")
    private List<String> tags;

    public TopCategory getTopCategoryEnum() {
        return TopCategory.from(this.gender);
    }

    public MiddleCategory getMiddleCategoryEnum() {
        return MiddleCategory.from(this.category);
    }

    public BottomCategory getBottomCategoryEnum() {
        return BottomCategory.from(this.subCategory);
    }

    public ProductConditionCategory getProductConditionEnum() {
        return ProductConditionCategory.from(this.productCondition);
    }
}