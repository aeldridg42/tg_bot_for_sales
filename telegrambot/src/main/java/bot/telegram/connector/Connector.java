package bot.telegram.connector;

import lombok.Getter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Getter
public class Connector {
    private URL url;
    private HttpURLConnection httpURLConnection;

    public Connector(String url) {
        try {
            this.url = new URL(url);
            httpURLConnection = (HttpURLConnection) this.url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("User-Agent", "PostmanRuntime/7.31.1");
        } catch (MalformedURLException e) {
            this.url = null;
        } catch (IOException e) {
            this.url = null;
            this.httpURLConnection = null;
        }
    }
}
