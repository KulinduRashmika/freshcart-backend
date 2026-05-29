package backend.controller;

import backend.entity.Cart;
import backend.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Get user's cart
    @GetMapping("/{userId}")
    public ResponseEntity<?> getMyCart(@PathVariable Long userId) {
        try {
            List<Cart> items = cartService.getUserCart(userId);
            return ResponseEntity.ok(items);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Add item to cart
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {

        try {

            cartService.addToCart(userId, productId, quantity);

            return ResponseEntity.ok(
                    Map.of("message", "Added to cart"));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Remove item
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> removeItem(@PathVariable Long cartId) {

        try {

            cartService.removeFromCart(cartId);

            return ResponseEntity.ok(
                    Map.of("message", "Item removed"));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Clear cart
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {

        try {

            cartService.clearCart(userId);

            return ResponseEntity.ok(
                    Map.of("message", "Cart cleared"));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}