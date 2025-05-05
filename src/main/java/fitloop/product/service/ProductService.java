package fitloop.product.service;

import fitloop.member.entity.ProfileEntity;
import fitloop.member.entity.UserEntity;
import fitloop.member.jwt.JWTUtil;
import fitloop.member.repository.ProfileRepository;
import fitloop.member.repository.UserRepository;
import fitloop.product.dto.request.ProductRegisterRequest;
import fitloop.product.dto.response.ProductDetailResponse;
import fitloop.product.dto.response.ProductRecentResponse;
import fitloop.product.entity.*;
import fitloop.product.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductConditionRepository productConditionRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRelationRepository productCategoryRelationRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductTagRepository productTagRepository;
    private final JWTUtil jwtUtil;
    private final ProfileRepository profileRepository;


    @Transactional
    public ResponseEntity<?> createProduct(ProductRegisterRequest productRegisterRequest, Object principal, String accessToken) {
        String username = jwtUtil.getUsername(accessToken);

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 카테고리 저장
        CategoryEntity category = CategoryEntity.builder()
                .topCategory(productRegisterRequest.getTopCategoryEnum())
                .middleCategory(productRegisterRequest.getMiddleCategoryEnum())
                .bottomCategory(productRegisterRequest.getBottomCategoryEnum())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        categoryRepository.save(category);

        // 상품 상태 저장
        ProductConditionEntity productCondition = ProductConditionEntity.builder()
                .productConditionCategory(productRegisterRequest.getProductConditionEnum())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        productConditionRepository.save(productCondition);

        // 상품 이미지 저장
        List<ProductImageEntity> productImageEntities = new ArrayList<>();
        for (String imageUrl : productRegisterRequest.getImages()) {
            ProductImageEntity productImageEntity = ProductImageEntity.builder()
                    .imageURL(imageUrl)
                    .build();

            productImageEntities.add(productImageEntity);
        }

        // 상품 태그 저장
        List<ProductTagEntity> productTagEntities = new ArrayList<>();
        for (String tag : productRegisterRequest.getTags()) {
            ProductTagEntity productTagEntity = ProductTagEntity.builder()
                    .tagName(tag)
                    .build();

            productTagEntities.add(productTagEntity);
        }

        // 상품 저장
        ProductEntity product = ProductEntity.builder()
                .userEntity(userEntity)
                .name(productRegisterRequest.getProductName())
                .price(productRegisterRequest.getPrice())
                .isFree(productRegisterRequest.isFree())
                .description(productRegisterRequest.getProductDescription())
                .productConditionEntity(productCondition)
                .isActive(true)
                .isSold(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .includeShipping(productRegisterRequest.isInCludeShipping())
                .build();
        productRepository.save(product);

        // 이미지의 productEntity를 연결하고 저장
        for (ProductImageEntity productImageEntity : productImageEntities) {
            productImageEntity.setProductEntity(product);
            productImageRepository.save(productImageEntity);
        }

        // 태그의 productEntity를 연결하고 저장
        for (ProductTagEntity productTagEntity : productTagEntities) {
            productTagEntity.setProductEntity(product);
            productTagRepository.save(productTagEntity);
        }

        // 상품 - 카테고리 관계 저장
        ProductCategoryRelationEntity relation = ProductCategoryRelationEntity.builder()
                .productEntity(product)
                .categoryEntity(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        productCategoryRelationRepository.save(relation);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "상품 등록 성공"));
    }

    public List<ProductRecentResponse> getRecentProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductEntity> productPage = productRepository.findAllByIsActiveTrue(pageable);

        List<ProductEntity> products = productPage.getContent();
        List<Long> productIds = products.stream().map(ProductEntity::getId).toList();

        List<ProductImageEntity> allImages = productImageRepository.findByProductEntityIdIn(productIds);
        Map<Long, List<String>> imageMap = allImages.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getProductEntity().getId(),
                        Collectors.mapping(ProductImageEntity::getImageURL, Collectors.toList())
                ));

        List<ProductTagEntity> allTags = productTagRepository.findByProductEntityIdIn(productIds);
        Map<Long, List<String>> tagMap = allTags.stream()
                .collect(Collectors.groupingBy(
                        tag -> tag.getProductEntity().getId(),
                        Collectors.mapping(ProductTagEntity::getTagName, Collectors.toList())
                ));

        return products.stream()
                .map(product -> ProductRecentResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .isFree(product.isFree())
                        .includeShipping(product.isIncludeShipping())
                        .likeCount(product.getLikeCount())
                        .createdAt(product.getCreatedAt())
                        .imageUrls(imageMap.getOrDefault(product.getId(), List.of()))
                        .tags(tagMap.getOrDefault(product.getId(), List.of()))
                        .build())
                .toList();
    }

    public ProductDetailResponse getProductDetail(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 1. 이미지 URL 리스트
        List<String> imageUrls = productImageRepository.findAllByProductEntity(product)
                .stream()
                .map(ProductImageEntity::getImageURL)
                .toList();

        // 2. 태그 리스트
        List<String> tags = productTagRepository.findAllByProductEntity(product)
                .stream()
                .map(ProductTagEntity::getTagName)
                .toList();

        // 3. 카테고리 정보 (Top > Middle > Bottom)
        ProductCategoryRelationEntity relation = productCategoryRelationRepository.findByProductEntity(product)
                .orElseThrow(() -> new IllegalArgumentException("카테고리 연관 정보를 찾을 수 없습니다."));
        CategoryEntity category = relation.getCategoryEntity();
        String categoryDescription = category.getTopCategory().getDescription() + " > "
                + category.getMiddleCategory().getDescription() + " > "
                + category.getBottomCategory().getDescription();

        // 4. 판매자 닉네임
        ProfileEntity profile = profileRepository.findByUserId(product.getUserEntity())
                .orElseThrow(() -> new IllegalArgumentException("프로필 정보가 없습니다."));

        // 5. 최종 응답 빌드
        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .includeShipping(product.isIncludeShipping())
                .likeCount(product.getLikeCount())
                .createdAt(product.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .imageUrls(imageUrls)
                .tags(tags)
                .free(product.isFree())
                .description(product.getDescription())
                .category(categoryDescription)
                .sellerName(profile.getNickname())
                .rating(4) // TODO: 리뷰 데이터로 연동 시 수정
                .reviewCount(13) // TODO: 리뷰 개수 연동 시 수정
                .condition(product.getProductConditionEntity()
                                .getProductConditionCategory()
                                .getDescription()
                )
                .build();
    }
}
