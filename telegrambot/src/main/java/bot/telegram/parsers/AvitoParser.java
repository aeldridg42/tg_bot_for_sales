package bot.telegram.parsers;

import bot.telegram.models.Image;
import bot.telegram.models.Product;
import bot.telegram.utils.ImageUpload;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

@RequiredArgsConstructor
public class AvitoParser extends Parser {
    private final String url;

    @Override
    public Optional<Product> parse() {
        Product product = new Product();

        product.setUrl(url);
        if (httpURLConnection == null) {
            return Optional.empty();
        }
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));
            stringsHandler(in, product);
            in.close();
        } catch (Exception e) {
            return Optional.empty();
        }

        return Optional.of(product);
    }

    private void stringsHandler(BufferedReader in, Product product) throws Exception {
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
        if (product.getDescription().length() > 200)
            product.setDescription("было слишком большое описание"); //todo
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
        imageHandler(temp.substring("style=\"background-image:url(".length(), temp.indexOf(")\">")), product);
    }

    private void imageHandler(String pictureUrl, Product product) throws IOException {
        Image image = new Image();

        image.setPath(ImageUpload.upload(pictureUrl));
        ImageUpload.correctImageRes(image.getPath(), product);
        product.addImageToProduct(image);
    }
}
