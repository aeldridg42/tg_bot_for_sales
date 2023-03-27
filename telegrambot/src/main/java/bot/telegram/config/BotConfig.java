package bot.telegram.config;

import bot.telegram.services.ProductService;
import bot.telegram.services.TelegramBot;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {
    private String botPath;
    private String botUsername;
    private String botToken;

    @Bean
    public TelegramBot telegramBot(ProductService productService) {
        TelegramBot telegramBot = new TelegramBot(botToken);
        telegramBot.setBotUsername(botUsername);
        telegramBot.setBotPath(botPath);
        telegramBot.setProductService(productService);

        return telegramBot;
    }
}


