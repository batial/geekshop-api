package batial.geekshop.api.service;

import batial.geekshop.api.exception.ApiException;
import batial.geekshop.api.model.*;
import batial.geekshop.api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import batial.geekshop.api.dto.request.OrderItemRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final UserService userService;
    private final ProductVariantService variantService;

    @Transactional
    public Order create(UUID userId, List<OrderItemRequest> itemsRequest, String shippingAddress, String city, String phone) {

        User user = userService.findById(userId);

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .city(city)
                .phone(phone)
                .status(Order.OrderStatus.PENDING)
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : itemsRequest) {
            UUID productId = UUID.fromString(itemRequest.getProductId());
            Integer quantity = itemRequest.getQuantity();

            Product product = productService.findById(productId);
            BigDecimal itemPrice;

            if (itemRequest.getVariantId() != null && !itemRequest.getVariantId().isEmpty()) {
                UUID variantId = UUID.fromString(itemRequest.getVariantId());
                ProductVariant variant = variantService.getVariantById(variantId);

                if (variant.getStock() < quantity) {
                    throw new ApiException(
                            "Insufficient stock for variant: " + variant.getSize() + " " + variant.getColor(),
                            HttpStatus.BAD_REQUEST
                    );
                }

                variantService.decreaseStock(variantId, quantity);

                itemPrice = variant.getFinalPrice();

            } else {
                productService.updateStock(productId, quantity);
                itemPrice = product.getPrice();
            }

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(itemPrice)
                    .build();

            order.getItems().add(item);
            total = total.add(itemPrice.multiply(BigDecimal.valueOf(quantity)));
        }

        order.setTotal(total);
        return orderRepository.save(order);
    }

    public Page<Order> findByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findByUserId(userId, pageable);
    }

    public Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));
    }

    public Page<Order> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findAll(pageable);
    }

    @Transactional
    public Order updateStatus(UUID id, Order.OrderStatus status) {
        Order order = findById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}