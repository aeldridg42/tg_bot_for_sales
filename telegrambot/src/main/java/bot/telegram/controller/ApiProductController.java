package bot.telegram.controller;

import bot.telegram.models.Product;
import bot.telegram.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ApiProductController {
    private final ProductService productService;

    @GetMapping("/api/products")
    public List<Product> getAllProducts() {
        List<Product> products = productService.getAll();
        products.forEach(p -> p.setImages(null));
        return products;
    }

    @GetMapping("/api/products/{id}")
    public Optional<Product> getProductById(@PathVariable int id) {
        return productService.getProduct(id);
    }

    @DeleteMapping("/api/products/{id}")
    public void deleteProductById(@PathVariable int id) {
        productService.remove(id);
    }

    @PostMapping("/api/products/create")
    public Product createProduct(@RequestBody Product product) {
        return productService.save(product);
    }

    @PutMapping("/api/products/{id}")
    public Product updateProduct(@RequestBody Product product,
                                 @PathVariable int id) {
        product.setId(id);
        return productService.save(product); //todo
    }

}
