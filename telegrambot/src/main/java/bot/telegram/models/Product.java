package bot.telegram.models;

import jakarta.persistence.*;
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
    private String name;
    private String category;
    private String description;
    private double price;
    private String url;
    private Long last_updated;
    private boolean isManual = false;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
    private List<Image> images = new ArrayList<>();

    private Integer previewImageId;

    @Override
    public String toString() {
        return "name: " + name +
                "\ncategory: " + category +
                "\nprice: " + price +
                "\n" + url;
    }
}
