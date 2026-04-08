package batial.geekshop.api.dto.response;

import batial.geekshop.api.model.Category;
import java.util.UUID;

public class CategoryResponse {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private Boolean hasVariants;


    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.slug = category.getSlug();
        this.description = category.getDescription();
        this.hasVariants = category.getHasVariants();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() {return description;}

    public Boolean getHasVariants() {return hasVariants;}
}