package fitloop.cart.service;

import fitloop.cart.dto.response.CartItemResponse;
import fitloop.cart.entity.CartEntity;
import fitloop.cart.repository.CartRepository;
import fitloop.member.auth.MemberIdentity;
import fitloop.member.entity.ProfileEntity;
import fitloop.member.entity.UserEntity;
import fitloop.member.repository.ProfileRepository;
import fitloop.member.repository.UserRepository;
import fitloop.product.entity.CategoryEntity;
import fitloop.product.entity.ProductCategoryRelationEntity;
import fitloop.product.entity.ProductEntity;
import fitloop.product.entity.ProductImageEntity;
import fitloop.product.repository.ProductCategoryRelationRepository;
import fitloop.product.repository.ProductImageRepository;
import fitloop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRelationRepository categoryRelationRepository;
    private final ProductImageRepository productImageRepository;
    private final ProfileRepository profileRepository;

    public ResponseEntity<?> addToCart(MemberIdentity member, Long productId) {
        Long userId = member.id();

        boolean exists = cartRepository.findByUserIdAndProductId(userId, productId).isPresent();
        if (!exists) {
            CartEntity cart = CartEntity.builder()
                    .userId(userId)
                    .productId(productId)
                    .build();
            cartRepository.save(cart);
        }

        return ResponseEntity.ok().build();
    }

    public List<CartItemResponse> getCartItems(MemberIdentity member) {
        Long userId = member.id();
        List<CartEntity> cartItems = cartRepository.findByUserId(userId);
        List<Long> productIds = cartItems.stream().map(CartEntity::getProductId).toList();

        Map<Long, List<String>> imageMap = productImageRepository.findByProductEntityIdIn(productIds).stream()
                .collect(Collectors.groupingBy(
                        image -> image.getProductEntity().getId(),
                        Collectors.mapping(ProductImageEntity::getImageURL, Collectors.toList())
                ));

        return cartItems.stream()
                .map(cart -> {
                    ProductEntity product = productRepository.findById(cart.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("상품 없음"));
                    UserEntity seller = product.getUserEntity();

                    ProfileEntity profile = profileRepository.findByUserId(seller)
                            .orElseThrow(() -> new IllegalArgumentException("판매자 프로필 없음"));

                    ProductCategoryRelationEntity relation = categoryRelationRepository.findByProductEntity(product)
                            .orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));
                    CategoryEntity category = relation.getCategoryEntity();

                    String categoryDescription = category.getTopCategory().getDescription() + " > " +
                            category.getMiddleCategory().getDescription() + " > " +
                            category.getBottomCategory().getDescription();

                    return CartItemResponse.builder()
                            .cartId(cart.getId())
                            .productId(product.getId())
                            .productName(product.getName())
                            .imageUrls(imageMap.getOrDefault(product.getId(), List.of()))
                            .category(categoryDescription)
                            .price((int) product.getPrice())
                            .sellerNickname(profile.getNickname())
                            .sellerProfileImage(profile.getProfileImage())
                            .sellerId(seller.getId())
                            .build();
                })
                .toList();
    }

    public ResponseEntity<?> removeFromCart(MemberIdentity member, Long productId) {
        Long userId = member.id();
        cartRepository.deleteByUserIdAndProductId(userId, productId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> clearCart(MemberIdentity member) {
        Long userId = member.id();
        List<CartEntity> items = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(items);
        return ResponseEntity.ok().build();
    }
}
