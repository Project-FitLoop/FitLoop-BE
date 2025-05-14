package fitloop.cart.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CartItemResponse {
    private Long cartId;
    private Long productId;
    private Long sellerId;
    private String productName;
    private List<String> imageUrls;
    private String category;
    private Integer price;
    private String sellerNickname;
    private String sellerProfileImage;
}