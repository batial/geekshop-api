package batial.geekshop.api.dto.request;

import java.util.Map;
import java.util.UUID;

public class OrderRequest {
    private String shippingAddress;
    private Map<UUID, Integer> items;

    public String getShippingAddress() { return shippingAddress; }
    public Map<UUID, Integer> getItems() { return items; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setItems(Map<UUID, Integer> items) { this.items = items; }
}