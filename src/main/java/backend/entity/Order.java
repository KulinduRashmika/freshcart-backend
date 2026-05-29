package backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String customerName;

    private String address;

    private String phone;                    // ← Add this field

    private Double totalAmount;

    private LocalDateTime orderDate;

    @Column(columnDefinition = "TEXT")
    private String orderItems;
}