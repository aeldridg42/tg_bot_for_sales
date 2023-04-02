package bot.telegram.parsers;

import bot.telegram.models.Product;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AvitoParserTest {
    final String VALID = "https://www.avito.ru/kazan/odezhda_obuv_aksessuary/krossovki_nike_air_force_1_belye_3028442154";
    final String INVALID = "https://www.avito.ru/random";
    @Test
    void parse() {
        Parser parser = Parser.getInstance(VALID);
        Optional<Product> product = parser.parse();
        assertTrue(product.isPresent());

        parser.httpURLConnection = null;
        assertTrue(parser.parse().isEmpty());

        parser = Parser.getInstance(INVALID);
        product = parser.parse();
        assertTrue(product.isEmpty());
    }
}