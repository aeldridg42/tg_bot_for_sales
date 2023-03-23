package bot.telegram;

import bot.telegram.models.Product;
import bot.telegram.parsers.Parser;
import bot.telegram.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainTest {
    @Autowired
    ProductRepository productRepository;
    public static void main(String[] args) {
        Parser parser = Parser.getInstance("https://www.avito.ru/kazan/odezhda_obuv_aksessuary/nike_air_force_1_2723812254");
        Product product = parser.parse();
        MainTest mainTest = new MainTest();
        mainTest.productRepository.save(product);
    }
}
