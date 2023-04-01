package bot.telegram.config;

import bot.telegram.services.ProductService;
import bot.telegram.services.TelegramBot;
import bot.telegram.services.UserService;
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
    private String adminKey;
    private String webappUrl;
    private String webhookPath;

    @Bean
    public TelegramBot telegramBot(ProductService productService, UserService userService) {
        TelegramBot telegramBot = new TelegramBot(botToken);
        telegramBot.setBotUsername(botUsername);
        telegramBot.setBotPath(botPath);
        telegramBot.setAdminKey(adminKey);
        telegramBot.setWebappUrl(webappUrl);
        telegramBot.setWebhookPath(webhookPath);
        telegramBot.setProductService(productService);
        telegramBot.setUserService(userService);

        return telegramBot;
    }
}


