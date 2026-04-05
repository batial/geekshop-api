package batial.geekshop.api.dto.request;

import java.util.Map;
import java.util.UUID;

public class OrderRequest {
    private String shippingAddress;
    private String city;
    private String phone;
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