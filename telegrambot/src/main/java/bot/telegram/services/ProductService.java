package bot.telegram.services;

import bot.telegram.models.Product;
import bot.telegram.parsers.Parser;
import bot.telegram.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public void save(Product product) {
        if (product.getId() != null)
            product.setLast_updated(new Date().getTime());
        productRepository.save(product);
    }

    public Optional<Product> findByUrl(String url) {
        return productRepository.findAll().stream().filter(product -> product.getUrl().equals(url)).findAny();
    }

    public Optional<Product> getProduct(String url) {
        Optional<Product> product = findByUrl(url);
        long currentTime = new Date().getTime();
        if (product.isPresent() && currentTime - product.get().getLast_updated() > 7200000) {
            Optional<Product> product1 = Parser.getInstance(url).parse();
            int id = product.get().getId();
            if (product1.isPresent()) {
                product1.get().setId(id);
                save(product1.get());
            }
            else {
                productRepository.deleteById(id);
            }
        } else if (product.isEmpty()) {
            product = Parser.getInstance(url).parse();
            product.ifPresent(this::save);
        }
        return product;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

}
