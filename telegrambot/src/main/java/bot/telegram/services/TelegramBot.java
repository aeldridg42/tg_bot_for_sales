package bot.telegram.services;

import bot.telegram.models.Product;
import bot.telegram.models.User;
import bot.telegram.repositories.ImageRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
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
    private String webhookPath;
    private ProductService productService;
    private ImageRepository imageRepository;
    private UserService userService;

    private final String PERM_D = "В доступе отказано.";
    private final String ALR_ADM = "Вы и так являетесь администратором!";
    private final String WR_ARG = "Недопустимое количество аргументов.";
    private final String NOT_RD = "Данный функционал в разработке! Попробуйте позднее.";
    private final String ADD_SCS = "Товар успешно добавлен в каталог!";
    private final String RM_SCS = "Товар был удален из каталога (если существовал)!";
    private final String WR_URL = "Некорректная ссылка! Возможно данный товар не поддерживается ботом.";
    private final String WLCM = "Стартуем!";
    private final String NEW_ADM = """
            Вы стали администратором данного магазина!
            Вам стали доступны следующие команды:
            \\add "ссылка на товар" - позволяет добавить товар в каталог магазина.
            \\delete "ссылка на товар" - позволяет удалить товар из каталога.""";

    public TelegramBot(String botToken) {
        super(botToken);
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "приветственное сообщение"));
        listOfCommands.add(new BotCommand("/help", "информация"));
        listOfCommands.add(new BotCommand("/show", "получить список товаров"));
        listOfCommands.add(new BotCommand("/webapp", "открыть магазин"));
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
        InlineKeyboardMarkup keyboardMarkup = null;

        if (update.getMessage() != null && update.getMessage().hasText()) {
            String[] messageSplit = update.getMessage().getText().split(" ");
            switch (messageSplit[0]) {
                case "/admin" -> {
                    if (user.getRole() == User.ROLE.ADMIN) {
                        answer.append(ALR_ADM);
                    } else if (messageSplit.length > 1 && becomeAdmin(user, messageSplit[1])) {
                        answer.append(NEW_ADM);
                    } else {
                        answer.append(PERM_D);
                    }
                }
                case "/add" -> {
                    if (user.getRole() != User.ROLE.ADMIN) {
                        answer.append(PERM_D);
                    } else if (messageSplit.length != 2) {
                        answer.append(WR_ARG);
                    } else {
                        Optional<Product> product = productService.saveProduct(messageSplit[1]);
                        if (product.isEmpty()) {
                            answer.append(WR_URL);
                        } else {
                            answer.append(ADD_SCS);
                        }
                    }
                }
                case "/show" -> {
                    List<Product> productList = productService.getAll();
                    for (Product product : productList) {
                        answer.append(product).append("\n");
                    }
                }
                case "/delete" -> {
                    if (user.getRole() != User.ROLE.ADMIN) {
                        answer.append(PERM_D);
                    } else if (messageSplit.length != 2) {
                        answer.append(WR_ARG);
                    } else {
                        productService.remove(messageSplit[1]);
                        answer.append(RM_SCS);
                    }
                }
                case "/help", "/info" -> answer.append(NOT_RD);
                case "/start" -> answer.append(WLCM);
//                case "/edit" -> {
//                    if (user.getRole() != User.ROLE.ADMIN) {
//                        answer.append(PERM_D);
//                    } else {
//                        keyboardMarkup = createKeyboardMarkup(webhookPath + "/products");
//                        answer.append("Открыть меню редактирования товаров");
//                    }
//                }
                case "/webapp2" -> {
                    keyboardMarkup = createKeyboardMarkup(webappUrl);
                    answer.append("Открыть магазин");
                }
                case "/webapp" -> {
                    ReplyKeyboardMarkup keyboardMarkup2 = new ReplyKeyboardMarkup();
                    keyboardMarkup2.setResizeKeyboard(true);
                    List<KeyboardRow> keyboardRows = new ArrayList<>();
                    KeyboardRow keyboardRow = new KeyboardRow();
                    KeyboardButton keyboardButton = new KeyboardButton();
                    WebAppInfo webAppInfo = new WebAppInfo(webappUrl);
                    keyboardButton.setWebApp(webAppInfo);
                    keyboardButton.setText("Go to the shop");
                    keyboardRow.add(keyboardButton);
                    keyboardRows.add(keyboardRow);
                    keyboardMarkup2.setKeyboard(keyboardRows);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chat_id);
                    sendMessage.setText("Site");
                    sendMessage.setReplyMarkup(keyboardMarkup2);
                    try {
                        execute(sendMessage);
                        return null;
                    } catch (TelegramApiException e) {
                        log.error("Error occured: " + e.getMessage());
                    }
                }
                case "/edit" -> {
//                    ReplyKeyboardMarkup keyboardMarkup3 = new ReplyKeyboardMarkup();
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> lists = new ArrayList<>();
                    List<InlineKeyboardButton> list = new ArrayList<>();
                    InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                    inlineKeyboardButton.setText("Web application");
                    inlineKeyboardButton.setUrl("https://f56c-176-52-22-229.eu.ngrok.io/products");
//                    inlineKeyboardButton.setWebApp(new WebAppInfo(url));
                    list.add(inlineKeyboardButton);
                    lists.add(list);
                    inlineKeyboardMarkup.setKeyboard(lists);

//                    keyboardMarkup3.setResizeKeyboard(true);
//                    List<KeyboardRow> keyboardRows2 = new ArrayList<>();
//                    KeyboardRow keyboardRow2 = new KeyboardRow();
//                    KeyboardButton keyboardButton2 = new KeyboardButton();
//                    WebAppInfo webAppInfo2 = new WebAppInfo("https://legal-pumas-bake-176-52-21-180.loca.lt/products");
//                    keyboardButton2.setWebApp(webAppInfo2);
//                    keyboardButton2.setText("Go to the shop");
//                    keyboardRow2.add(keyboardButton2);
//                    keyboardRows2.add(keyboardRow2);
//                    keyboardMarkup3.setKeyboard(keyboardRows2);
                    SendMessage sendMessage1 = new SendMessage();
                    sendMessage1.setChatId(chat_id);
                    sendMessage1.setText("Site");
                    sendMessage1.setReplyMarkup(inlineKeyboardMarkup);
                    try {
                        execute(sendMessage1);
                        return null;
                    } catch (TelegramApiException e) {
                        log.error("Error occured: " + e.getMessage());
                    }
                }
            }
        }
        if (update.getMessage().getWebAppData() != null) {
            try {
                int data = Integer.parseInt(update.getMessage().getWebAppData().getData());
                Optional<Product> product = productService.getProduct(data);
                if (product.isPresent())
                    answer.append("Вы выбрали: ").append("\n").append(product.get());
                else {
                    answer.append("Продукт не найден :(");
                }
                sendPhoto(chat_id, answer.toString(), product.get().getPreviewImageId());
                return null;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }
        sendMessage(chat_id, answer.toString(), keyboardMarkup);
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

    private void sendMessage(long chatId, String messageToSend, InlineKeyboardMarkup keyboardMarkup) {
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

    private void sendPhoto(long chatId, String messageToSend, int id) {
        InputFile inputFile = new InputFile(new File(imageRepository.findById(id).get().getPath()));
        SendPhoto sendPhoto = new SendPhoto(String.valueOf(chatId), inputFile);
        sendPhoto.setCaption(messageToSend);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error("Error occured: " + e.getMessage());
        }
    }

    private InlineKeyboardMarkup createKeyboardMarkup(String url) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        List<InlineKeyboardButton> list = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Web application");
        inlineKeyboardButton.setWebApp(new WebAppInfo(url));
        list.add(inlineKeyboardButton);
        lists.add(list);
        inlineKeyboardMarkup.setKeyboard(lists);
        return inlineKeyboardMarkup;
    }

}
