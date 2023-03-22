package bot.telegram;

import bot.telegram.parsers.Parser;

public class MainTest {
    public static void main(String[] args) {
        Parser parser = Parser.getInstance("https://www.avito.ru/kazan/odezhda_obuv_aksessuary/nike_air_force_1_2723812254");
        parser.parse();
    }
}
