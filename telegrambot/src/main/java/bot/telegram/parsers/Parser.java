package bot.telegram.parsers;

import bot.telegram.parsers.connector.Connector;
import bot.telegram.models.Product;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Optional;

public abstract class Parser {
    protected HttpURLConnection httpURLConnection;
    protected String url;
    private String category;
    public static Parser getInstance(String url, String category) {
        Parser parser = null;

        if (url.toLowerCase().contains("avito.ru")) {
            parser = new AvitoParser();
            parser.httpURLConnection = new Connector(url).getHttpURLConnection();
        }
        else if (url.toLowerCase().contains("barberkit.ru")) {
            parser = new BarberkitParser();
            parser.url = url;
            parser.httpURLConnection = new Connector(url).getHttpURLConnection();
        }
        if (parser != null)
            parser.category = category;
        return parser;
    }


    public Optional<Product> parse() {
        Product product = new Product();

        product.setUrl(url);
        product.setCategory(category);
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

    abstract void stringsHandler(BufferedReader in, Product product) throws Exception;
}
