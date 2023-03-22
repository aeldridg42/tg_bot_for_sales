package bot.telegram.parsers;

import bot.telegram.connector.Connector;
import bot.telegram.models.Product;

import java.net.HttpURLConnection;

public abstract class Parser {
    protected HttpURLConnection httpURLConnection;
    public static Parser getInstance(String url) {
        Parser parser = new AvitoParser(url);
        parser.httpURLConnection = new Connector(url).getHttpURLConnection();
        return parser;
    }

    public abstract Product parse();
}
