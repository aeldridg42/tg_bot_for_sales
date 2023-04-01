package bot.telegram.controller;

import bot.telegram.models.Product;
import bot.telegram.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products")
    public String index(Model model) {
        model.addAttribute("products", productService.getAll());
        return "index";
    }

    @GetMapping("/products/{id}")
    public String show(Model model,
                       @PathVariable int id) {
        model.addAttribute("product", productService.getProduct(id));
        return "show";
    }

    @PostMapping("/products")
    public void create(@RequestBody Product product) {
        product.setUrl("manual");
        productService.save(product);
    }

    @PutMapping("/products/{id}")
    public void update(@RequestBody Product product,
                       @PathVariable int id) {
        product.setId(id);
        product.setUrl("manual");
        productService.save(product);
    }

    @DeleteMapping("/products/{id}")
    public void delete(@PathVariable int id) {
        productService.remove(id);
    }
}
