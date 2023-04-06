package bot.telegram.controller;

import bot.telegram.models.Product;
import bot.telegram.services.ProductService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products")
    public String index(Model model) {
        model.addAttribute("products", productService.getAll());
        model.addAttribute("newProduct", new Product());
        return "index";
    }

    @GetMapping("/products/{id}")
    public String show(Model model,
                       @PathVariable("id") int id) {
        Optional<Product> product = productService.getProduct(id);
        if (product.isEmpty()) {
            return "redirect:/products"; //todo
        }
        model.addAttribute("product", product.get());
        return "show";
    }

    @PostMapping("/products")
    @SneakyThrows
    public String create(@ModelAttribute Product product, MultipartFile file) {
        productService.saveFromController(product, file);
        return "redirect:/products";
    }

    @PatchMapping(path = "/products/{id}")
    public String update(@ModelAttribute Product product,
                         @PathVariable("id") int id) {
        product.setId(id);
        product.setManual(true);
        productService.update(product, true);
        return "redirect:/products";
    }

    @DeleteMapping("/products/{id}")
    public String delete(@PathVariable int id) {
        productService.remove(id);
        return "redirect:/products";
    }
}
