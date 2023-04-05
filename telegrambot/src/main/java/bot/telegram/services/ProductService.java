package bot.telegram.services;

import bot.telegram.models.Image;
import bot.telegram.models.Product;
import bot.telegram.parsers.Parser;
import bot.telegram.repositories.ProductRepository;
import bot.telegram.utils.ImageUpload;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final static long UPD_TIME = 720000;

    public Optional<Product> saveProduct(String url) {
        Optional<Product> productAfterParse = Parser.getInstance(url).parse();
        return productAfterParse.map(this::save);
    }

    public void saveFromController(Product product, MultipartFile multipartFile) {
        product.setManual(true);
        product.setLast_updated(new Date().getTime());
        if (!multipartFile.isEmpty()) {
            Image image = new Image();
            image.setPath(ImageUpload.upload(multipartFile));
            ImageUpload.correctImageRes(image.getPath(), product);
            product.addImageToProduct(image);
        }
        save(product);
    }

    public Product save(Product product) {
        if ((product.getId() != null && productRepository.existsById(product.getId()))
            || (product.getUrl() != null && productRepository.existsByUrl(product.getUrl()))) {
            return update(product);
        }
        Product product1 = productRepository.save(product);
        List<Image> images = product1.getImages();
        if (images.size() > 0) {
            product1.setPreviewImageId(product1.getImages().get(0).getId());
        }
        product1.setLast_updated(new Date().getTime());
        return productRepository.save(product1);
    }

    public Product update(Product product) {
        Optional<Product> productFromBd = product.getId() == null ?
                productRepository.findByUrl(product.getUrl()) : productRepository.findById(product.getId());

        Product product1 = productFromBd.get();
        product1.setLast_updated(new Date().getTime());
        product1.setName(product.getName());
        product1.setDescription(product.getDescription());
        product1.setCategory(product.getCategory());
        product1.setPrice(product.getPrice());
        product1.setLast_updated(new Date().getTime());
        try {
            ImageUpload.delete(product.getImages().get(0).getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        int imageId = product1.getImageId();
//        product.getImage().setId(imageId);
//        product1.setImage(product.getImage());

        return productRepository.save(product1);
    }

    public Optional<Product> findByUrl(String url) {
        return productRepository.findByUrl(url);
    }

    public Optional<Product> getProduct(int id) {
        return productRepository.findById(id);
    }


    public List<Product> getAll() {
        List<Product> products = productRepository.findAll();
        long currentTime = new Date().getTime();
        for (int i = 0; i < products.size(); i++) {
            if (!products.get(i).isManual() && currentTime - products.get(i).getLast_updated() > UPD_TIME) {
                int id = products.get(i).getId();
                Product product = Parser.getInstance(products.get(i).getUrl()).parse().orElse(new Product(-1));
                if (product.getId() == null) {
                    product.setId(id);
                    product = save(product);
                }
                products.set(i, product);
            }
        }
        products.removeIf(p -> p.getId() != null && p.getId() == -1);
        return products;
    }


    public void remove(String url) {
        Optional<Product> product = findByUrl(url);
        product.ifPresent(p -> productRepository.deleteById(p.getId()));
    }

    public void remove(int id) {
        productRepository.deleteById(id);
    }

    public Product update(Product product, boolean flag) {
        Product productFromDB = productRepository.findById(product.getId()).get();
        productFromDB.setName(product.getName());
        productFromDB.setDescription(product.getDescription());
        productFromDB.setPrice(product.getPrice());
        productFromDB.setCategory(product.getCategory());
        productFromDB.setManual(flag);
        productFromDB.setLast_updated(new Date().getTime());
        return productRepository.save(productFromDB);
    }
}
