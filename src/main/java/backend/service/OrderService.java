package backend.service;

import backend.entity.Cart;
import backend.entity.Order;
import backend.entity.Product;
import backend.repository.CartRepository;
import backend.repository.OrderRepository;
import backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        UserRepository userRepository,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    @Transactional
    public Order placeOrder(Long userId, String customerName, String address, String phone) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        // Get user's cart items
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot place order.");
        }

        // Calculate total amount and prepare order items JSON
        double totalAmount = 0.0;
        List<Map<String, Object>> orderItemsList = new ArrayList<>();

        for (Cart cart : cartItems) {
            Product product = cart.getProduct();
            if (product == null) {
                throw new RuntimeException("Product not found for cart item: " + cart.getId());
            }

            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("productId", product.getId());
            itemMap.put("productName", product.getName());
            itemMap.put("quantity", cart.getQuantity());
            itemMap.put("price", product.getPrice());
            itemMap.put("total", product.getPrice() * cart.getQuantity());
            orderItemsList.add(itemMap);
            totalAmount += product.getPrice() * cart.getQuantity();
        }

        try {
            String orderItemsJson = objectMapper.writeValueAsString(orderItemsList);

            // Create order
            Order order = new Order();
            order.setUserId(userId);
            order.setCustomerName(customerName);
            order.setAddress(address);
            order.setPhone(phone);
            order.setTotalAmount(totalAmount);
            order.setOrderDate(LocalDateTime.now());
            order.setOrderItems(orderItemsJson);

            Order savedOrder = orderRepository.save(order);

            // Clear user's cart after successful order placement
            cartRepository.deleteByUserId(userId);

            return savedOrder;
        } catch (Exception e) {
            throw new RuntimeException("Failed to place order: " + e.getMessage());
        }
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

    public List<Order> getAllOrders() {

        return orderRepository.findAll(
                Sort.by(Sort.Direction.DESC, "orderDate")
        );
    }
}