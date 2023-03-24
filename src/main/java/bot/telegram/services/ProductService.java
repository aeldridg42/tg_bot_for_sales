package bot.telegram.services;

import bot.telegram.models.Product;
import bot.telegram.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public void save(Product product) {
        if (productRepository.findAll().stream().noneMatch(p -> p.getName().equals(product.getName())))
            productRepository.save(product);
    }

}
