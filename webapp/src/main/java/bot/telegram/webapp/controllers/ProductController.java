package bot.telegram.webapp.controllers;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
public class ProductController {
    private final String PATH = "/home/aeldridg/tg_bot_for_sales/webapp/target/classes/static/temp.json";
    private final String URL_PRODUCT = "https://174b-176-52-22-229.eu.ngrok.io/api/products";
    private final boolean DEV = true;
    @GetMapping("/")
    public String index() throws IOException {
        if (DEV) {
            File targetFile1 = new File(PATH);
//            FileUtils.delete(targetFile1);
            URL url1 = new URL(URL_PRODUCT);
            HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            targetFile1.createNewFile();
            FileUtils.copyInputStreamToFile(inputStream, targetFile1);
        }
        return "index";
    }
}
