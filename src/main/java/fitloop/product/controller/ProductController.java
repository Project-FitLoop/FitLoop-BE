package fitloop.product.controller;

import fitloop.product.dto.request.ProductRegisterRequest;
import fitloop.product.dto.response.ProductRecentResponse;
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
    public ResponseEntity<List<ProductRecentResponse>> getRecentProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        return ResponseEntity.ok(productService.getRecentProducts(page, size));
    }
}
