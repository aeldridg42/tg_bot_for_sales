package bot.telegram.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private String description;
    private double price;
    private String url;
    private Date lastUpdated;
    private String pictureUrl;

    public Product() {

    }

    @Override
    public String toString() {
        return "name: " + name +
                "\ncategory: " + category +
                "\ndescription: " + description +
                "\nprice: " + price +
                "\npicture: " + pictureUrl;
    }
}
