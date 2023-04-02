package bot.telegram.parsers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void getInstance() {
        Parser parser = Parser.getInstance("https://www.avito.ru/kazan/odezhda_obuv_aksessuary/krossovki_nike_air_force_1_belye_3028442154");
        assertInstanceOf(AvitoParser.class, parser);
        parser = Parser.getInstance("something");
        assertNull(parser);
    }

}