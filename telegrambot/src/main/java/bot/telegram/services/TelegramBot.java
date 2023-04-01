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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Setter
@Slf4j
public class TelegramBot extends TelegramWebhookBot {
    private String botPath;
    private String botUsername;
    private String adminKey;
    private String webappUrl;
    private ProductService productService;
    private UserService userService;

    public TelegramBot(String botToken) {
        super(botToken);
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "welcome message"));
        listOfCommands.add(new BotCommand("/help", "information"));
        listOfCommands.add(new BotCommand("/show", "get all products"));
        listOfCommands.add(new BotCommand("/webapp", "open shop"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }

    }
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        Long chat_id = update.getMessage().getChatId();
        User user = userService.getUser(chat_id);
        StringBuilder answer = new StringBuilder();

        if (update.getMessage() != null && update.getMessage().hasText()) {
            String[] messageSplit = update.getMessage().getText().split(" ");
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
                        answer.append("Permission denied");
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
                } case "/delete" -> {
                    if (user.getRole() != User.ROLE.ADMIN) {
                        answer.append("Permission denied");
                    } else if (messageSplit.length != 2) {
                        answer.append("Wrong number of arguments");
                    } else {
                        productService.remove(messageSplit[1]);
                        answer.append("Product removed if existed");
                    }
                } case "/help" -> answer.append("no help yet");
                case "/info" -> answer.append("no info yet");
                case "/webapp" -> {
                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                    replyKeyboardMarkup.setResizeKeyboard(true);
                    KeyboardButton keyboardButton = new KeyboardButton();
                    WebAppInfo webAppInfo = new WebAppInfo(webappUrl);
                    keyboardButton.setWebApp(webAppInfo);
                    keyboardButton.setText("Site");
                    KeyboardRow keyboardRow = new KeyboardRow();
                    keyboardRow.add(keyboardButton);
                    List<KeyboardRow> keyboardRows = new ArrayList<>();
                    keyboardRows.add(keyboardRow);
                    replyKeyboardMarkup.setKeyboard(keyboardRows);
                    sendMessage(chat_id, "Site", replyKeyboardMarkup);
                    return null;
                }
            }
//            sendMessage(chat_id, answer.toString(), null);
        }
        if (update.getMessage().getWebAppData() != null) {
            try {
                int data = Integer.parseInt(update.getMessage().getWebAppData().getData());
                Optional<Product> product = productService.findById(data);
                if (product.isPresent())
                    answer.append("Вы выбрали: ").append("\n").append(product);
                else {
                    answer.append("Продукт не найден :(");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        sendMessage(chat_id, answer.toString(), null);
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
            log.info(user.getChatId() + " became an administrator.");
            return true;
        }
        return false;
    }

    private void sendMessage(long chatId, String messageToSend, ReplyKeyboardMarkup keyboardMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageToSend);
        if (keyboardMarkup != null)
            message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occured: " + e.getMessage());
        }
    }
}
