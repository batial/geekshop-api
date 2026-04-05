package batial.geekshop.api.dto.response;

import java.util.UUID;

public class AuthResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String token;

    public AuthResponse(UUID id, String firstName, String lastName, String email, String role, String token) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getlastName() { return lastName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getToken() { return token; }
}