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
        model.addAttribute("newProduct", new Product());
        return "index";
    }

    @GetMapping("/products/{id}")
    public String show(Model model,
                       @PathVariable("id") int id) {
        model.addAttribute("product", productService.getProduct(id).get()); //todo
        return "show";
    }

    @PostMapping("/products")
    public String create(@ModelAttribute Product product) {
        product.setUrl("manual");
        product.setManual(true);
        product.setLast_updated(1L);
        productService.save(product);
        return "redirect:/products";
    }

    @PostMapping("/products/{id}")
    public String update(@ModelAttribute Product product,
                       @PathVariable int id) {
        product.setId(id);
        product.setUrl("manual");
        product.setManual(true);
        productService.save(product);
        return "redirect:/products";
    }

    @DeleteMapping("/products/{id}")
    public String delete(@PathVariable int id) {
        productService.remove(id);
        return "redirect:/products";
    }
}
