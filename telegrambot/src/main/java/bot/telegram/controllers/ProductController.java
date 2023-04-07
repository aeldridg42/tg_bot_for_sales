package bot.telegram.controllers;

import bot.telegram.models.Product;
import bot.telegram.services.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @GetMapping("/products/create")
    public String newProduct(@ModelAttribute("newProduct") Product newProduct) {
        return "new";
    }

    @PostMapping("/products/create")
    @SneakyThrows
    public String create(@ModelAttribute("newProduct") @Valid Product product,
                         BindingResult bindingResult, MultipartFile file) {
        if (bindingResult.hasErrors()) {
            return "new";
        }
        productService.saveFromController(product, file);
        return "redirect:/products";
    }

    @PatchMapping(path = "/products/{id}")
    public String update(@ModelAttribute @Valid Product product, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            return "show";
        }
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
