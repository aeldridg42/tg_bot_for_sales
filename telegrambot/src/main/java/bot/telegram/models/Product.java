package bot.telegram.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
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
    private String picture_url;

    @Override
    public String toString() {
        return "name: " + name +
                "\ncategory: " + category +
//                "\ndescription: " + description +
                "\nprice: " + price +
//                "\npicture: " + picture_url +
                "\n" + url;
    }
}
