package bot.telegram.parsers;

import bot.telegram.models.Image;
import bot.telegram.models.Product;
import bot.telegram.utils.ImageUpload;
import bot.telegram.utils.TextEdit;

import java.io.BufferedReader;
import java.io.IOException;

public class AvitoParser extends Parser {
    @Override
    void stringsHandler(BufferedReader in, Product product) throws Exception {
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
        if (TextEdit.hasMarkups(product.getName()))
            product.setName(TextEdit.removeMarkups(product.getName()));
        content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        //описание
        temp = new StringBuilder(content.substring(content.indexOf("itemProp=\"description\"><p>")));
        product.setDescription(temp.substring("itemProp=\"description\"><p>".length(), temp.indexOf("</p>")));
        if (TextEdit.hasMarkups(product.getDescription())) {
            product.setDescription(TextEdit.removeMarkups(product.getDescription()));
        }
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
