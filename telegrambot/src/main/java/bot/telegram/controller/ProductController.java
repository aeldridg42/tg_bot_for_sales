package bot.telegram.controller;

import bot.telegram.models.Product;
import bot.telegram.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/api/products")
    public ResponseEntity<List<Product>> showAll() {
        return ResponseEntity.accepted()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.getAll());
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<Optional<Product>> show(@PathVariable int id) {
        return ResponseEntity.accepted()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.findById(id));
    }
}
