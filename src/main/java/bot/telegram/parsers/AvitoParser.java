package bot.telegram.parsers;

import bot.telegram.models.Product;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
            stringsHandler(in, product);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(product);

        return product;
    }

    private void stringsHandler(BufferedReader in, Product product) throws IOException {
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null && !inputLine.startsWith("<link rel=\"stylesheet\"")) {
            content.append(inputLine);
        }
        //цена
        StringBuilder temp = new StringBuilder(content.substring(content.indexOf("property=\"product:price:amount\" content=\"")));
        product.setPrice(Double.parseDouble(temp.substring("property=\"product:price:amount\" content=\"".length(),
                temp.indexOf("\"/>"))));
        //название
        temp = new StringBuilder(content.substring(content.indexOf("property=\"og:description\" content=\"")));
        product.setName(temp.substring("property=\"og:description\" content=\"".length(),
                temp.indexOf(": объявление")));

        content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        //описание
        temp = new StringBuilder(content.substring(content.indexOf("itemProp=\"description\"><p>")));
        product.setDescription(temp.substring("itemProp=\"description\"><p>".length(), temp.indexOf("</p>")));
        for (int i = 0; i < 3; i++) {
            content.replace(0, content.indexOf("class=\"breadcrumbs-link-Vr4Nc\"><span itemProp=\"name\">")
                    + "class=\"breadcrumbs-link-Vr4Nc\"><span itemProp=\"name\">".length(), "");
        }
        //категория
        temp = new StringBuilder(content.substring(content.indexOf("class=\"breadcrumbs-link-Vr4Nc\"><span itemProp=\"name\">")));
        product.setCategory(temp.substring("class=\"breadcrumbs-link-Vr4Nc\"><span itemProp=\"name\">".length(),
                temp.indexOf("</span>")).trim());
        //изображение
        temp = new StringBuilder(content.substring(content.indexOf("style=\"background-image:url(")));
        product.setPictureUrl(temp.substring("style=\"background-image:url(".length(),
                temp.indexOf(")\">")));
    }
}
