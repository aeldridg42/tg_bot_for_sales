package bot.telegram.services;

import bot.telegram.models.Product;
import bot.telegram.parsers.Parser;
import bot.telegram.parsers.connector.Connector;
import bot.telegram.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Barberkit {
    private final String BARBERKIT_URL = "https://barberkit.ru";
    private final Map<String, String> CATEGORIES = new HashMap<>();

    {
        CATEGORIES.put("/mashinki", "Машинки");
        CATEGORIES.put("/trimmer", "Триммеры");
        CATEGORIES.put("/shaver", "Шейверы");
        CATEGORIES.put("/hair_dryers", "Фены");
        CATEGORIES.put("/scissors", "Ножницы");
        CATEGORIES.put("/massage", "Массажеры");
        CATEGORIES.put("/combos", "Наборы");
        CATEGORIES.put("/nozhi", "Ножевые блоки");
        CATEGORIES.put("/peignoirs", "Пеньюары");
        CATEGORIES.put("/shavette", "Шаветки");
        CATEGORIES.put("/accessories", "Аксессуары");
    }

    private final ProductService productService;

    public void fillBarberkit() throws IOException {
        for (Map.Entry<String, String> category : CATEGORIES.entrySet()) {
            Connector connector = new Connector(BARBERKIT_URL + category.getKey());
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connector.getHttpURLConnection().getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            String[] splittedProducts = content.toString().split("<a data-action=\"link\"  href=\"");
            for (String string : splittedProducts) {
                if (!string.startsWith("/") || string.toLowerCase().contains("нет в наличии"))
                    continue;
                productService.saveProduct(BARBERKIT_URL + string.substring(0, string.indexOf("/\"")), category.getValue());
            }
        }
    }
}
