package bot.telegram.services;

import bot.telegram.models.Product;
import bot.telegram.parsers.Parser;
import bot.telegram.repositories.ProductRepository;
import lombok.Setter;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Setter
public class TelegramBot extends TelegramWebhookBot {
    private String botPath;
    private String botUsername;
    private ProductService productService;
    public TelegramBot(String botToken) {
        super(botToken);

    }
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            String chat_id = String.valueOf(update.getMessage().getChatId());

            try {
                Product product = Parser.getInstance(update.getMessage().getText()).parse();
                if (product.getName() != null) {
                    execute(new SendMessage(chat_id, product.toString()));
                    productService.save(product);
                }
                else {
                    execute(new SendMessage(chat_id, update.getMessage().getText()));
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
