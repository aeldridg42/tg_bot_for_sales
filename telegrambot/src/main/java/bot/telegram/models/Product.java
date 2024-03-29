package bot.telegram.models;

import bot.telegram.utils.ImageUpload;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    @Min(value = 1, message = "Пожалуйста добавьте корректную цену")
    @Max(value = Integer.MAX_VALUE, message = "Пожалуйста добавьте корректную цену")
    private double price;
    private String url;
    private Long last_updated;
    private boolean isManual = false;

    public Product(int id) {
        this.id = id;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
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
        ImageUpload.correctImageRes(image.getPath(), this, 140f, 190f);
    }

    @Override
    public String toString() {
        return name + "\n" +
                description +
                "\nЦена: " + (long)price + "рублей";
    }
}
