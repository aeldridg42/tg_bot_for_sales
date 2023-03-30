package bot.telegram.services;

import bot.telegram.models.Product;
import bot.telegram.models.User;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Setter
//@Slf4j
public class TelegramBot extends TelegramWebhookBot {
    private String botPath;
    private String botUsername;
    private String adminKey;
    private ProductService productService;
    private UserService userService;

    public TelegramBot(String botToken) {
        super(botToken);
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "welcome message"));
        listOfCommands.add(new BotCommand("/help", "information"));
        listOfCommands.add(new BotCommand("/show", "get all products"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
//            log.error("Error occurred: " + e.getMessage());
        }

    }
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        User user = userService.getUser(update.getMessage().getChatId());

        if (update.getMessage() != null && update.getMessage().hasText()) {
            String chat_id = String.valueOf(update.getMessage().getChatId());
            String[] messageSplit = update.getMessage().getText().split(" ");
            StringBuilder answer = new StringBuilder();
            switch (messageSplit[0]) {
                case "/admin" -> {
                    if (user.getRole() == User.ROLE.ADMIN) {
                        answer.append("You are already an admin");
                    } else if (messageSplit.length > 1 && becomeAdmin(user, messageSplit[1])) {
                        answer.append("You became an admin");
                    } else {
                        answer.append("Wrong keyword");
                    }
                }
                case "/add" -> {
                    if (user.getRole() != User.ROLE.ADMIN) {
                        answer.append("Please authorize before adding products!");
                    } else if (messageSplit.length != 2) {
                        answer.append("Wrong number of arguments");
                    } else {
                        Optional<Product> product = productService.getProduct(messageSplit[1]);
                        if (product.isEmpty()) {
                            answer.append("Wrong url");
                        } else {
                            answer.append("Product successfully added/updated");
                        }
                    }
                } case "/show" -> {
                    List<Product> productList = productService.getAll();
                    for (Product product : productList) {
                        answer.append(product).append("\n");
                    }
                }
            }
            try {
                execute(new SendMessage(chat_id, answer.toString()));
            } catch (TelegramApiException e) {
//                log.error("Error occurred: " + e.getMessage());
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

    public boolean becomeAdmin(User user, String key) {
        if (key.equals(this.adminKey)) {
            userService.setAdmin(user);
//            log.info("INFO: " + user.getChatId() + " became an administrator.");
            return true;
        }
        return false;
    }
}
