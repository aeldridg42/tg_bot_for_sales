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

import java.util.NoSuchElementException;
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
                         BindingResult bindingResult, @RequestParam("file") MultipartFile[] files) {
        if (bindingResult.hasErrors()) {
            return "new";
        }
        productService.saveFromController(product, files);
        return "redirect:/products";
    }

    @PatchMapping("/products/{id}")
    public String update(@ModelAttribute @Valid Product product, BindingResult bindingResult,
                         @PathVariable("id") int id, @RequestParam("file") MultipartFile[] files) {
        if (bindingResult.hasErrors()) {
            return "show";
        }
        product.setId(id);
        product.setManual(true);
        try {
            productService.update(product, true, files);
        } catch (NoSuchElementException ignore) {

        }
        return "redirect:/products";
    }

    @PutMapping("/products/{p_id}/images/{i_id}")
    public String setPreview(@PathVariable("p_id") int p_id, @PathVariable("i_id") int i_id) {
        try {
            productService.setImagePreview(p_id, i_id);
        } catch (NoSuchElementException ignore) {

        }
        return "redirect:/products/" + p_id;
    }

    @DeleteMapping("/products/{p_id}/images/{i_id}")
    public String deleteImage(@PathVariable("p_id") int p_id, @PathVariable("i_id") int i_id) {
        System.out.println(i_id);
        try {
            productService.deleteImage(p_id, i_id);
        } catch (NoSuchElementException ignore) {

        }
        return "redirect:/products/" + p_id;
    }

    @DeleteMapping("/products/{id}")
    public String delete(@PathVariable int id) {
        productService.remove(id);
        return "redirect:/products";
    }
}
