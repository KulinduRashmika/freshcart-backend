package backend.controller;

import backend.entity.Order;
import backend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(
            @RequestParam Long userId,
            @RequestParam String customerName,
            @RequestParam String address,
            @RequestParam String phone) {

        Order order = orderService.placeOrder(userId, customerName, address, phone);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders(@RequestParam Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}