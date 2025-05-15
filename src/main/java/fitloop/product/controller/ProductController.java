package fitloop.product.controller;

import fitloop.product.dto.request.ProductRegisterRequest;
import fitloop.product.dto.response.ProductDetailResponse;
import fitloop.product.dto.response.ProductResponse;
import fitloop.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/register")
    public ResponseEntity<?> createProduct(
            @RequestBody @Valid ProductRegisterRequest productRegisterRequest,
            @AuthenticationPrincipal Object principal,
            @RequestHeader("access") String accessToken) {
        return productService.createProduct(productRegisterRequest, principal, accessToken);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ProductResponse>> getRecentProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        return ResponseEntity.ok(productService.getRecentProducts(page, size));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ProductResponse>> getPopularProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        return ResponseEntity.ok(productService.getPopularProducts(page, size));
    }

    @GetMapping("/category")
    public ResponseEntity<List<ProductResponse>> getCategoryProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam int categoryCode,
            @RequestParam String gender
    ) {
        return ResponseEntity.ok(productService.getCategoryProducts(page, size, categoryCode, gender));
    }

    @GetMapping("/{id}")
    public ProductDetailResponse getProductDetail(@PathVariable Long id) {
        return productService.getProductDetail(id);
    }
}
