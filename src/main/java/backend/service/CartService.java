package backend.service;

import backend.entity.Cart;
import backend.entity.Product;
import backend.repository.CartRepository;
import backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        // Check if product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // Check if product already in cart using Optional
        Cart existingCart = cartRepository.findByUserIdAndProductId(userId, productId)
                .orElse(null);

        if (existingCart != null) {
            // Update quantity if product already in cart
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            return cartRepository.save(existingCart);
        } else {
            // Create new cart item
            Cart newCartItem = new Cart();
            newCartItem.setUserId(userId);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);
            return cartRepository.save(newCartItem);
        }
    }

    @Transactional
    public Cart updateCartItemQuantity(Long cartId, Integer quantity) {
        Cart cartItem = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with ID: " + cartId));

        if (quantity <= 0) {
            // If quantity is 0 or negative, remove the item
            cartRepository.deleteById(cartId);
            return null;
        }

        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    @Transactional
    public void removeFromCart(Long cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new RuntimeException("Cart item not found with ID: " + cartId);
        }
        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
    }

    public List<Cart> getUserCart(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public double getCartTotal(Long userId) {
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    public int getCartItemCount(Long userId) {
        return (int) cartRepository.countByUserId(userId);
    }
    // Add this method to CartService.java
    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with ID: " + cartId));
    }

    public void removeItem(Long cartId, Long userId) {

    }
}