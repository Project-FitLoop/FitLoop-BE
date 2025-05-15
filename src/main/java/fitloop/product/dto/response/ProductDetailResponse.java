package fitloop.product.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductDetailResponse {
    private Long id;
    private String name;
    private double price;
    private boolean includeShipping;
    private Long likeCount;
    private String createdAt;
    private List<String> imageUrls;
    private List<String> tags;
    private boolean free;
    private String description;
    private String category;
    private String sellerName;
    private int rating;
    private String profileImages;
    private int reviewCount;
    private String condition;
}

