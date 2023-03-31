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

    @GetMapping("/")
    public String index() throws IOException {
//        URL url1 = new URL("https://localhost:5000/api/products");
//        HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
//        InputStream inputStream = urlConnection.getInputStream();
//        File targetFile = new File("src/main/resources/json/temp.json");
//        FileUtils.copyInputStreamToFile(inputStream, targetFile);
        return "index";
    }
}
