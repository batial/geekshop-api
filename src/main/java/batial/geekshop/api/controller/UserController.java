package batial.geekshop.api.controller;

import batial.geekshop.api.dto.response.UserResponse;
import batial.geekshop.api.model.User;
import batial.geekshop.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList()));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateRole(@PathVariable UUID id,
                                                   @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(new UserResponse(
                userService.updateRole(id, User.Role.valueOf(body.get("role")))));
    }
}