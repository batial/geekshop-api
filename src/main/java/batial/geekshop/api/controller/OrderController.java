package batial.geekshop.api.controller;

import batial.geekshop.api.model.Order;
import batial.geekshop.api.model.User;
import batial.geekshop.api.security.JwtService;
import batial.geekshop.api.service.OrderService;
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
    public ResponseEntity<Order> create(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body) {

        String email = jwtService.extractEmail(authHeader.substring(7));
        User user = userService.findByEmail(email);

        Map<UUID, Integer> items = ((Map<String, Integer>) body.get("items"))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        e -> UUID.fromString(e.getKey()),
                        Map.Entry::getValue
                ));

        Order order = orderService.create(
                user.getId(),
                items,
                (String) body.get("shippingAddress")
        );

        return ResponseEntity.ok(order);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Order>> myOrders(
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(orderService.findByUser(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> updateStatus(@PathVariable UUID id,
                                              @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(orderService.updateStatus(
                id,
                Order.OrderStatus.valueOf(body.get("status"))
        ));
    }
}