package bot.telegram.models;

import bot.telegram.utils.ImageUpload;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotEmpty(message = "Название товара не может быть пустым")
    private String name;
    @NotEmpty(message = "Категория товара не может быть пустой")
    private String category;
    @NotEmpty(message = "Описание товара не может быть пустым")
    @Column(columnDefinition = "TEXT")
    private String description;
    private double price;
    private String url;
    private Long last_updated;
    private boolean isManual = false;

    public Product(int id) {
        this.id = id;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "product")
    private List<Image> images = new ArrayList<>();

    private Integer previewImageId;
    private Integer height;
    private Integer width;

    public void addImageToProduct(Image image) {
        image.setProduct(this);
        images.add(image);
    }

    public void setPreview(Image image) {
        this.previewImageId = image.getId();
        ImageUpload.correctImageRes(image.getPath(), this);
    }

    @Override
    public String toString() {
        return name + "\n" +
                description +
                "\nЦена: " + (long)price + "рублей";
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("category", category);
        result.put("description", description);
        result.put("price", price);
        result.put("previewImageId", previewImageId);
        result.put("height", height);
        result.put("width", width);
        List<Integer> images = new ArrayList<>();
        this.images.forEach(image -> images.add(image.getId()));
        result.put("images", images);

        return result;
    }


}
