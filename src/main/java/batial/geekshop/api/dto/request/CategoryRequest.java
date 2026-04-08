package batial.geekshop.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;
    private String description;
    private Boolean hasVariants;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getHasVariants() {return hasVariants;}
    public void setHasVariants(Boolean hasVariants) {this.hasVariants = hasVariants;}
}