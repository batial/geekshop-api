package batial.geekshop.api.controller;

import batial.geekshop.api.dto.request.LoginRequest;
import batial.geekshop.api.dto.request.RegisterRequest;
import batial.geekshop.api.dto.response.AuthResponse;
import batial.geekshop.api.model.User;
import batial.geekshop.api.security.JwtService;
import batial.geekshop.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = userService.register(request.getName(), request.getEmail(), request.getPassword());
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name(), token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = userService.findByEmail(request.getEmail());
        if (!userService.checkPassword(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).build();
        }
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name(), token));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@RequestHeader("Authorization") String authHeader) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name(), null));
    }
}