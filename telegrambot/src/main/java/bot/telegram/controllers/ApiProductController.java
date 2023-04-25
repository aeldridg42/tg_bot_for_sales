package bot.telegram.controllers;

import bot.telegram.models.Product;
import bot.telegram.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ApiProductController {
    private final ProductService productService;

    @GetMapping("/api/products")
    public List<Map<String, Object>> getAllProducts() {
        List<Product> products = productService.getAll();

        List<Map<String, Object>> result = new ArrayList<>();
        products.forEach(p -> result.add(productService.toMap(p)));
        return result;
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
