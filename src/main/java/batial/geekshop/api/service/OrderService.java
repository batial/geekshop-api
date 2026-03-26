package batial.geekshop.api.service;

import batial.geekshop.api.model.*;
import batial.geekshop.api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final UserService userService;

    @Transactional
    public Order create(UUID userId, Map<UUID, Integer> items, String shippingAddress) {

        User user = userService.findById(userId);

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .status(Order.OrderStatus.PENDING)
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<UUID, Integer> entry : items.entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productService.findById(productId);
            productService.updateStock(productId, quantity);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .build();

            order.getItems().add(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        order.setTotal(total);
        return orderRepository.save(order);
    }

    public List<Order> findByUser(UUID userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order updateStatus(UUID id, Order.OrderStatus status) {
        Order order = findById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}