package bot.telegram.connector;

import bot.telegram.parsers.connector.Connector;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorTest {
    final String VALID = "https://www.avito.ru/kazan/odezhda_obuv_aksessuary/krossovki_nike_air_force_1_belye_3028442154";
    final String INVALID = "123";
    @Test
    void getUrl() {
        Connector connector = new Connector(VALID);
        assertEquals(VALID, connector.getUrl().toString());
        Connector connector1 = new Connector(INVALID);
        assertNull(connector1.getUrl());
    }

    @Test
    void getHttpURLConnection() throws IOException {
        Connector connector = new Connector(VALID);
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(VALID).openConnection();
        assertEquals(connector.getHttpURLConnection().getURL(), httpURLConnection.getURL());
        Connector connector1 = new Connector(INVALID);
        assertNull(connector1.getHttpURLConnection());
    }
}