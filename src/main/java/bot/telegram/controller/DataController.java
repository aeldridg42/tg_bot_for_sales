package bot.telegram.controller;

import bot.telegram.models.Product;
import bot.telegram.services.ProductService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class DataController {
    private final ProductService productService;

    @GetMapping("/products")
    public List<Product> index() {
        return productService.getAll();
    }
}
