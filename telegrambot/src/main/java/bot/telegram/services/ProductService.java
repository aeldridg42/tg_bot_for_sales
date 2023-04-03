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

    public Product save(Product product) {
        Product productFromDB = productRepository.save(product);
        productFromDB.setPreviewImageId(productFromDB.getImages().get(0).getId());
        productFromDB.setLast_updated(new Date().getTime());
        return productRepository.save(productFromDB);
    }

    public Optional<Product> findByUrl(String url) {
        return productRepository.findByUrl(url);
    }

    public Optional<Product> getProduct(String url) {
        Optional<Product> product = findByUrl(url);
        long currentTime = new Date().getTime();
        if (product.isPresent()
                && !product.get().isManual()
                && currentTime - product.get().getLast_updated() > UPD_TIME) {
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

    public Optional<Product> getProduct(int id) {
        Optional<Product> product = productRepository.findById(id);
        long currentTime = new Date().getTime();
        if (product.isPresent()
                && !product.get().isManual()
                && currentTime - product.get().getLast_updated() > UPD_TIME) {
            product = Parser.getInstance(product.get().getUrl()).parse();
            if (product.isPresent()) {
                product.get().setId(id);
                save(product.get());
            } else {
                productRepository.deleteById(id);
            }
        }
        return product;
    }

    public List<Product> getAll() {
        List<Product> products = productRepository.findAll();
//        long currentTime = new Date().getTime();
//        products.removeIf(product -> currentTime - product.getLast_updated() > UPD_TIME
//                && getProduct(product.getUrl()).isEmpty());
        return products;
    }

    public Optional<Product> findById(int id) {
        return productRepository.findById(id);
    }

    public void remove(String url) {
        Optional<Product> product = findByUrl(url);
        product.ifPresent(p -> productRepository.deleteById(p.getId()));
    }

    public void remove(int id) {
        productRepository.deleteById(id);
    }
}
