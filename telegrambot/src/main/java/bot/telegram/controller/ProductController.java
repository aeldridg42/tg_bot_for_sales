package bot.telegram.controller;

import bot.telegram.models.Image;
import bot.telegram.models.Product;
import bot.telegram.services.ProductService;
import bot.telegram.utils.ImageUpload;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @SneakyThrows
    public String create(@ModelAttribute Product product, MultipartFile file) {
        product.setManual(true);
        product.setLast_updated(1L);
        Image image = new Image();
        image.setPath(ImageUpload.upload(file));
        product.addImageToProduct(image);
        productService.save(product);
        return "redirect:/products";
    }

    @PostMapping(path = "/products/{id}")
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
