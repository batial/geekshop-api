package batial.geekshop.api.controller;

import batial.geekshop.api.dto.request.OrderRequest;
import batial.geekshop.api.dto.response.OrderResponse;
import batial.geekshop.api.model.Order;
import batial.geekshop.api.model.User;
import batial.geekshop.api.security.JwtService;
import batial.geekshop.api.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import batial.geekshop.api.service.UserService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;
    private final UserService userService;

    public OrderController(OrderService orderService, JwtService jwtService, UserService userService) {
        this.orderService = orderService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody OrderRequest request) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        User user = userService.findByEmail(email);
        Order order = orderService
                .create(user.getId(), request.getItems(), request.getShippingAddress(), request.getCity(),request.getPhone());
        return ResponseEntity.ok(new OrderResponse(order));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<OrderResponse>> myOrders(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(
                orderService.findByUser(user.getId(), page, size).map(OrderResponse::new)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(new OrderResponse(orderService.findById(id)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                orderService.findAll(page, size).map(OrderResponse::new)
        );
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable UUID id,
                                                      @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(new OrderResponse(
                orderService.updateStatus(id, Order.OrderStatus.valueOf(body.get("status")))));
    }
}