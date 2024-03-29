package bot.telegram.services;

import bot.telegram.models.Image;
import bot.telegram.models.Product;
import bot.telegram.parsers.Parser;
import bot.telegram.repositories.ProductRepository;
import bot.telegram.utils.ImageUpload;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ImageService imageService;
    private final static long UPD_TIME = 7200000;

    public Optional<Product> saveProduct(String url, String category) {
        Parser parser = Parser.getInstance(url, category);
        Optional<Product> productAfterParse = parser != null ? parser.parse() : Optional.empty();
        return productAfterParse.map(this::save);
    }

    public void saveFromController(Product product, MultipartFile[] files) {
        product.setManual(true);
        product.setLast_updated(new Date().getTime());
        if (product.getUrl() == null) {
            product.setUrl("manual");
        }
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
                || (product.getUrl() != null && !product.getUrl().equals("manual")
                && productRepository.existsByUrl(product.getUrl()))) {
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
        Product product1                = productFromBd.orElseThrow();

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

        return productRepository.save(product1);
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
                Product product = Parser.getInstance(products.get(i).getUrl(), products.get(i).getCategory()).parse()
                        .orElse(new Product(-1));
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


    public void remove(String name) {
        Optional<Product> product = productRepository.findByName(name);

        product.ifPresent(this::remove);
    }

    public void remove(int id) {
        Optional<Product> product = productRepository.findById(id);

        product.ifPresent(this::remove);
    }

    private void remove(Product product) {
        for (Image image : product.getImages()) {
            imageService.deleteImage(image);
        }
        productRepository.deleteById(product.getId());
    }

    public void setImagePreview(Product product, Image image) {
        product.setPreviewImageId(image.getId());
        ImageUpload.correctImageRes(image.getPath(), product, 140f, 190f);
        productRepository.save(product);
    }

    public void setImagePreview(int p_id, int i_id) {
        Product product = productRepository.findById(p_id).orElseThrow();
        for (Image image : product.getImages()) {
            if (image.getId() == i_id) {
                product.setPreview(image);
            }
        }
        productRepository.save(product);
    }

    public void deleteImage(int p_id, int i_id) {
        Product product = productRepository.findById(p_id).orElseThrow();
        boolean change = product.getPreviewImageId() == i_id;
        product.getImages().removeIf(image -> image.getId() == i_id);
        imageService.deleteImage(i_id);
        if (change && product.getImages().size() > 0) {
            setImagePreview(product, product.getImages().get(0));
        } else {
            productRepository.save(product);
        }
    }

    public void removeAll() {
        List<Product> products = productRepository.findAll();
        products.forEach(this::remove);
    }

    public Map<String, Object> toMap(Product product) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", product.getId());
        result.put("name", product.getName());
        result.put("category", product.getCategory());
        result.put("description", product.getDescription());
        result.put("price", product.getPrice());
        result.put("previewImageId", product.getPreviewImageId());
        result.put("height", product.getHeight());
        result.put("width", product.getWidth());
        List<Map<String, Integer>> images = new ArrayList<>();
        product.getImages().forEach(image -> images.add(getMap(image)));
        result.put("images", images);

        return result;
    }

    private Map<String, Integer> getMap(Image image) {
        Map<String, Integer> imageMap = new HashMap<>();
        imageMap.put("id", image.getId());
        ImageUpload.correctImageRes(imageService.findById(image.getId()).orElseThrow(), imageMap, 300f, 300f);

        return imageMap;
    }
}
