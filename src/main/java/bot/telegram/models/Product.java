package bot.telegram.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class Product {
    private Long id;
    private String name;
    private String category;
    private String description;
    private double price;
    private final String url;
    private Date lastUpdated;

    public Product(String url) {
        this.url = url;
    }
}
