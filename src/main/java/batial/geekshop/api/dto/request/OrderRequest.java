package batial.geekshop.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public class OrderRequest {
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9+\\s\\-]{6,20}$", message = "Phone number is not valid")
    private String phone;

    @NotNull(message = "Items are required")
    @Size(min = 1, message = "Order must have at least one item")
    private Map<UUID, Integer> items;

    public String getShippingAddress() { return shippingAddress; }
    public String getCity() { return city; }
    public String getPhone() { return phone; }
    public Map<UUID, Integer> getItems() { return items; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setItems(Map<UUID, Integer> items) { this.items = items; }
    public void setCity(String city) {this.city = city;}
    public void setPhone(String phone) {this.phone = phone;}
}