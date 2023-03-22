package bot.telegram.parsers;

import bot.telegram.models.Product;
import lombok.RequiredArgsConstructor;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;

@RequiredArgsConstructor
public class AvitoParser extends Parser {
    private final String url;

    @Override
    public Product parse() {
        Product product = new Product(url);
        if (httpURLConnection == null) {
            return product;
        }
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null && !inputLine.startsWith("<link rel=\"stylesheet\"")) {
                content.append(inputLine);
                System.out.println(inputLine);
            }

            String price = content.substring(content.indexOf("property=\"product:price:amount\" content=\""));
            product.setPrice(Double.parseDouble(price.substring("property=\"product:price:amount\" content=\"".length(), price.indexOf("\"/><meta data"))));
            product.setName(content.substring(content.indexOf("property=\"og:description\" content=\"")));
            product.setName(product.getName().substring("property=\"og:description\" content=\"".length(), product.getName().indexOf(": объявление")));
            content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            product.setCategory(content.substring(content.indexOf("Вид товара<!-- -->: </span>")));
            product.setCategory(product.getCategory().substring("Вид товара<!-- -->: </span>".length(), product.getCategory().indexOf("</li><li")).trim());
            product.setDescription(content.substring(content.indexOf("itemProp=\"description\"><p>")));
            product.setDescription(product.getDescription().substring("itemProp=\"description\"><p>".length(), product.getDescription().indexOf("</p>")));

            System.out.println("Name=" + product.getName());
            System.out.println("Category=" + product.getCategory());
            System.out.println("Price=" + product.getPrice());
            System.out.println("Description=" + product.getDescription());

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return product;
    }
}
