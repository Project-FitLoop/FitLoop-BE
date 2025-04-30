package fitloop.product.dto.response;

import fitloop.product.entity.ProductEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProductRecentResponse {
    private Long id;
    private String name;
    private double price;
    private boolean isFree;
    private boolean includeShipping;
    private Long likeCount;
    private LocalDateTime createdAt;
    private List<String> imageUrls;
    private List<String> tags;
}
