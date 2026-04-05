package batial.geekshop.api.dto.response;

import batial.geekshop.api.model.User;
import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    public UserResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole().name();
    }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getlastName() { return lastName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}