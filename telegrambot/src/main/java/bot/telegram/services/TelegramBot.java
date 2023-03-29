package bot.telegram.services;

import bot.telegram.models.Product;
import bot.telegram.models.User;
import lombok.Setter;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

@Setter
public class TelegramBot extends TelegramWebhookBot {
    private String botPath;
    private String botUsername;
    private ProductService productService;
    private UserService userService;
    public TelegramBot(String botToken) {
        super(botToken);

    }
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        User user = userService.getUser(update.getMessage().getChatId());
        System.out.println(user.getRole());

        if (update.getMessage() != null && update.getMessage().hasText()) {
            String chat_id = String.valueOf(update.getMessage().getChatId());

            try {
                Optional<Product> product = productService.getProduct(update.getMessage().getText());
                if (product.isPresent()) {
                    execute(new SendMessage(chat_id, product.toString()));
                }
                else {
                    List<Product> productList = productService.getAll();
                    for (Product product1 : productList) {
                        execute(new SendMessage(chat_id, product1.toString()));
                    }
                }
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public String getBotPath() {
        return botPath;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
