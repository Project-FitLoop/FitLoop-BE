package fitloop.product.controller;

import fitloop.product.dto.request.ProductRegisterRequest;
import fitloop.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}
