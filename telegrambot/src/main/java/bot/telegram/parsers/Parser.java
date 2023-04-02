package bot.telegram.parsers;

import bot.telegram.connector.Connector;
import bot.telegram.models.Product;

import java.net.HttpURLConnection;
import java.util.Optional;

public abstract class Parser {
    protected HttpURLConnection httpURLConnection;
    public static Parser getInstance(String url) {
        Parser parser = null;
        if (url.toLowerCase().startsWith("https://www.avito.ru/")) {
            parser = new AvitoParser(url);
            parser.httpURLConnection = new Connector(url).getHttpURLConnection();
        }
        return parser;
    }

    public abstract Optional<Product> parse();
}
