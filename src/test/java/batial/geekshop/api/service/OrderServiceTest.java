package batial.geekshop.api.service;

import batial.geekshop.api.exception.ApiException;
import batial.geekshop.api.model.*;
import batial.geekshop.api.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderService;

    private User buildUser() {
        return User.builder()
                .name("Sebas")
                .email("sebas@mail.com")
                .passwordHash("hashed")
                .role(User.Role.CUSTOMER)
                .build();
    }

    private Product buildProduct(int stock) {
        return Product.builder()
                .name("Remera Naruto")
                .description("Remera negra")
                .price(new BigDecimal("29.99"))
                .stock(stock)
                .type(Product.ProductType.SHIRT)
                .active(true)
                .build();
    }

    @Test
    void create_shouldCreateOrder_withCorrectTotal() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        User user = buildUser();
        Product product = buildProduct(50);

        Map<UUID, Integer> items = Map.of(productId, 2);

        when(userService.findById(userId)).thenReturn(user);
        when(productService.findById(productId)).thenReturn(product);
        when(productService.updateStock(productId, 2)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.create(userId, items, "Calle 123");

        assertThat(result.getTotal()).isEqualByComparingTo("59.98");
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getShippingAddress()).isEqualTo("Calle 123");
        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        verify(productService, times(1)).updateStock(productId, 2);
    }

    @Test
    void create_shouldSetCorrectUnitPrice() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        User user = buildUser();
        Product product = buildProduct(50);

        Map<UUID, Integer> items = Map.of(productId, 3);

        when(userService.findById(userId)).thenReturn(user);
        when(productService.findById(productId)).thenReturn(product);
        when(productService.updateStock(productId, 3)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.create(userId, items, "Calle 123");

        OrderItem item = result.getItems().get(0);
        assertThat(item.getUnitPrice()).isEqualByComparingTo("29.99");
        assertThat(item.getQuantity()).isEqualTo(3);
        assertThat(result.getTotal()).isEqualByComparingTo("89.97");
    }

    @Test
    void findById_shouldReturnOrder_whenExists() {
        UUID id = UUID.randomUUID();
        Order order = Order.builder()
                .status(Order.OrderStatus.PENDING)
                .total(new BigDecimal("59.98"))
                .shippingAddress("Calle 123")
                .build();

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        Order result = orderService.findById(id);

        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        assertThat(result.getTotal()).isEqualByComparingTo("59.98");
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(id))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    void findByUser_shouldReturnUserOrders() {
        UUID userId = UUID.randomUUID();
        Order order1 = Order.builder().status(Order.OrderStatus.PENDING).total(BigDecimal.TEN).shippingAddress("Calle 1").build();
        Order order2 = Order.builder().status(Order.OrderStatus.CONFIRMED).total(BigDecimal.TEN).shippingAddress("Calle 2").build();

        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order1, order2));

        List<Order> result = orderService.findByUser(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        assertThat(result.get(1).getStatus()).isEqualTo(Order.OrderStatus.CONFIRMED);
    }

    @Test
    void updateStatus_shouldChangeOrderStatus() {
        UUID id = UUID.randomUUID();
        Order order = Order.builder()
                .status(Order.OrderStatus.PENDING)
                .total(BigDecimal.TEN)
                .shippingAddress("Calle 123")
                .build();

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.updateStatus(id, Order.OrderStatus.CONFIRMED);

        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.CONFIRMED);
        verify(orderRepository, times(1)).save(order);
    }
}