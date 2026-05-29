package backend.repository;

import backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Find orders by userId, ordered by date descending (newest first)
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    // Find all orders ordered by date descending
    List<Order> findAllByOrderByOrderDateDesc();
}