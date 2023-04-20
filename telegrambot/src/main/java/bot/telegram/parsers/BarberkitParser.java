package bot.telegram.parsers;

import bot.telegram.models.Image;
import bot.telegram.models.Product;
import bot.telegram.utils.ImageUpload;
import bot.telegram.utils.TextEdit;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;

@RequiredArgsConstructor
public class BarberkitParser extends Parser {
    void stringsHandler(BufferedReader in, Product product) throws Exception {
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        //название
        StringBuilder tmp = new StringBuilder(content.substring(content.indexOf("<div class=\"title-1 text-block text-style-title\">") +
                "<div class=\"title-1 text-block text-style-title\">".length()));
        product.setName(tmp.substring(0, tmp.indexOf("</div>")));
        if (TextEdit.hasMarkups(product.getName())) {
            product.setName(TextEdit.removeMarkups(product.getName()));
        }

        //описание
        tmp = new StringBuilder(content.substring(content.indexOf("<div class=\"name text-block text-style-product-name\">") +
                "<div class=\"name text-block text-style-product-name\">".length()));
        product.setDescription(tmp.substring(0, tmp.indexOf("</div>", tmp.indexOf("</div>") + 5)));

        if (TextEdit.hasMarkups(product.getDescription())) {
            product.setDescription(TextEdit.removeMarkups(product.getDescription()));
        }

        //цена
        tmp = new StringBuilder(content.substring(0, content.indexOf("₽")));
        product.setPrice(Double.parseDouble(TextEdit.pricePrepare(tmp.substring(tmp.lastIndexOf(">") + 1))));


        //изображения
        int i = 0;
        while (content.indexOf("<div class=\"component-image img loading\" data-component=\"image\"  data-action-target  data-lightbox=\"") != -1 && ++i <= 5) {
            tmp = new StringBuilder(content.substring(content.indexOf("<div class=\"component-image img loading\" data-component=\"image\"  data-action-target  data-lightbox=\"")
                    + "<div class=\"component-image img loading\" data-component=\"image\"  data-action-target  data-lightbox=\"".length()));
            content.replace(0, content.indexOf("<div class=\"component-image img loading\" data-component=\"image\"  data-action-target  data-lightbox=\"")
                    + "<div class=\"component-image img loading\" data-component=\"image\"  data-action-target  data-lightbox=\"".length(), "");
            imageHandler(tmp.substring(0, tmp.indexOf("\"")), product);
        }
    }

    private void imageHandler(String pictureUrl, Product product) throws IOException {
        Image image = new Image();

        image.setPath(ImageUpload.upload("https://barberkit.ru" + pictureUrl));
        product.addImageToProduct(image);
    }
}
