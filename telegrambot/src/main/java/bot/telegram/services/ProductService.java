package bot.telegram.services;

import bot.telegram.models.Image;
import bot.telegram.models.Product;
import bot.telegram.parsers.Parser;
import bot.telegram.repositories.ProductRepository;
import bot.telegram.utils.ImageUpload;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ImageService imageService;
    private final static long UPD_TIME = 7200000;

    public Optional<Product> saveProduct(String url, String category) {
//        System.out.println(url);
        Parser parser = Parser.getInstance(url, category);
        Optional<Product> productAfterParse = parser != null ? parser.parse() : Optional.empty();
        return productAfterParse.map(this::save);
    }

    public void saveFromController(Product product, MultipartFile[] files) {
        product.setManual(true);
        product.setLast_updated(new Date().getTime());
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                Image image = new Image();
                image.setPath(ImageUpload.upload(file));
                product.addImageToProduct(image);
            }
        }

        save(product);
    }

    public Product save(Product product) {
        if ((product.getId() != null && productRepository.existsById(product.getId()))
            || (product.getUrl() != null && !product.getUrl().equals("") && productRepository.existsByUrl(product.getUrl()))) {
            return update(product, product.isManual(), null); //todo
        }

        Product product1    = productRepository.save(product);
        List<Image> images  = product1.getImages();

        if (images.size() > 0) {
            setImagePreview(product1, images.get(0));
        }
        product1.setLast_updated(new Date().getTime());
        return productRepository.save(product1);
    }

    public Product update(Product product, boolean flag, MultipartFile[] files) {
        Optional<Product> productFromBd = product.getId() == null ?
                productRepository.findByUrl(product.getUrl()) : productRepository.findById(product.getId());
        Product product1                = productFromBd.orElseThrow(); //todo

        product1.setLast_updated(new Date().getTime());
        product1.setName(product.getName());
        product1.setDescription(product.getDescription());
        product1.setCategory(product.getCategory());
        product1.setPrice(product.getPrice());
        product1.setLast_updated(new Date().getTime());
        product1.setManual(flag);
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    Image image = new Image();
                    image.setPath(ImageUpload.upload(file));
                    product1.addImageToProduct(image);
                }
            }
        }
//        try {
//            ImageUpload.delete(product.getImages().get(0).getPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
        List<Product> products  = productRepository.findAll();
        long currentTime        = new Date().getTime();

        for (int i = 0; i < products.size(); i++) {
            if (!products.get(i).isManual() && currentTime - products.get(i).getLast_updated() > UPD_TIME) {
                int id = products.get(i).getId();
                Product product = Parser.getInstance(products.get(i).getUrl(), "").parse().orElse(new Product(-1));
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
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty())
            return;
        for (Image image : product.get().getImages()) {
            imageService.deleteImage(image);
        }
        productRepository.deleteById(id);
    }

    public void setImagePreview(Product product, Image image) {
        product.setPreviewImageId(image.getId());
        ImageUpload.correctImageRes(image.getPath(), product);
        productRepository.save(product);
    }

    public void setImagePreview(int p_id, int i_id) {
        Product product = productRepository.findById(p_id).get(); //todo
        for (Image image : product.getImages()) {
            if (image.getId() == i_id) {
                product.setPreview(image);
            }
        }
        productRepository.save(product);
    }

    public void deleteImage(int p_id, int i_id) {
        Product product = productRepository.findById(p_id).get(); //todo
        boolean change = product.getPreviewImageId() == i_id;
        product.getImages().removeIf(image -> image.getId() == i_id);
        imageService.deleteImage(i_id);
        if (change && product.getImages().size() > 0) {
            setImagePreview(product, product.getImages().get(0));
        } else {
            productRepository.save(product);
        }
    }
}
