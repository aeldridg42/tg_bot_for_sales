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
    private final static long UPD_TIME = 7200000;

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
        if (product.isPresent() && currentTime - product.get().getLast_updated() > UPD_TIME) {
            int id = product.get().getId();
            product = Parser.getInstance(url).parse();
            if (product.isPresent()) {
                product.get().setId(id);
                save(product.get());
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
        List<Product> products = productRepository.findAll();
        long currentTime = new Date().getTime();
        products.removeIf(product -> currentTime - product.getLast_updated() > UPD_TIME
                && getProduct(product.getUrl()).isEmpty());
        return products;
    }

    public Optional<Product> findById(int id) {
        return productRepository.findById(id);
    }
}
