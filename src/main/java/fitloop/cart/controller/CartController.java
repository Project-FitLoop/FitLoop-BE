package fitloop.cart.controller;

import fitloop.cart.dto.response.CartItemResponse;
import fitloop.cart.service.CartService;
import fitloop.member.auth.Login;
import fitloop.member.auth.MemberIdentity;
import fitloop.member.auth.VerifiedMember;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    @Login
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @VerifiedMember MemberIdentity member,
            @RequestParam @NotNull Long productId
    ) {
        return cartService.addToCart(member, productId);
    }

    @Login
    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems(
            @VerifiedMember MemberIdentity member
    ) {
        return ResponseEntity.ok(cartService.getCartItems(member));
    }

    @Login
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(
            @VerifiedMember MemberIdentity member,
            @RequestParam @NotNull Long productId
    ) {
        return cartService.removeFromCart(member, productId);
    }

    @Login
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(
            @VerifiedMember MemberIdentity member
    ) {
        return cartService.clearCart(member);
    }
}
