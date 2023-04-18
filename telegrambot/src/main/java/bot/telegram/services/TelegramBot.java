package bot.telegram.services;

import bot.telegram.models.Image;
import bot.telegram.models.Product;
import bot.telegram.models.User;
import bot.telegram.repositories.ImageRepository;
import bot.telegram.utils.ImageUpload;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
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
    private Barberkit barberkit;

    private final String PERM_D     = "В доступе отказано.";
    private final String ALR_ADM    = "Вы и так являетесь администратором!";
    private final String WR_ARG     = "Недопустимое количество аргументов.";
    private final String NOT_RD     = "Данный функционал в разработке! Попробуйте позднее.";
    private final String ADD_SCS    = "Товар успешно добавлен в каталог!";
    private final String RM_SCS     = "Товар был удален из каталога (если существовал)!";
    private final String WR_URL     = "Некорректная ссылка! Возможно данный товар не поддерживается ботом.";
    private final String WLCM       = "Стартуем!";
    private final String NEW_ADM    = """
            Вы стали администратором данного магазина!
            Вам стали доступны следующие команды:
            \\add "ссылка на товар" - позволяет добавить товар в каталог магазина (на данный момент реализована поддержка только авито).
            \\delete "ссылка на товар" - позволяет удалить товар из каталога.
            \\edit - открыть в браузере меню для добавления или редактирования товаров""";

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
        Long chat_id                    = update.getMessage().getChatId();
        User user                       = userService.getUser(chat_id);
        StringBuilder answer            = new StringBuilder();
        ReplyKeyboard keyboardMarkup    = null;

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
                        Optional<Product> product = productService.saveProduct(messageSplit[1], "");
                        if (product.isEmpty()) {
                            answer.append(WR_URL);
                        } else {
                            answer.append(ADD_SCS);
                        }
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
                case "/barberkit" -> {
                    if (user.getRole() != User.ROLE.ADMIN) {
                        answer.append(PERM_D);
                    } else {
                        try {
                            barberkit.fillBarberkit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        answer.append("DONE!");
                    }
                }
                case "/help", "/info" -> answer.append(NOT_RD);
                case "/start" -> answer.append(WLCM);
                case "/webapp" -> {
                    keyboardMarkup = createReplyKeyboardMarkup(webappUrl, "Перейти в магазин");
                    answer.append("Открыть магазин");
                }
                case "/edit" -> {
                    if (user.getRole() != User.ROLE.ADMIN) {
                        answer.append(PERM_D);
                    } else {
                        keyboardMarkup = createInlineKeyboardMarkup(webhookPath + "/products", "Изменить каталог");
                        answer.append("Изменить каталог");
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
                if (product.get().getImages().size() > 1) {
                    sendPhotos(chat_id, product.get().getId(), answer.toString());
                } else {
                    sendPhoto(chat_id, answer.toString(), product.filter(value -> value.getPreviewImageId() != null).map(Product::getPreviewImageId).orElse(0));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return null;

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

    private void sendMessage(long chatId, String messageToSend, ReplyKeyboard keyboardMarkup) {
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText(messageToSend);
        if (keyboardMarkup != null)
            message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private void sendPhoto(long chatId, String messageToSend, int id) {
        Optional<Image> image   = imageRepository.findById(id);
        InputFile inputFile     = new InputFile(new File(image.isPresent() ? image.get().getPath()
                                    : ImageUpload.DEFAULT));
        SendPhoto sendPhoto     = new SendPhoto(String.valueOf(chatId), inputFile);

        sendPhoto.setCaption(messageToSend);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private void sendPhotos(long chatId, int productId, String message) {
        Product product = productService.getProduct(productId).get();
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        List<InputMedia> list = new ArrayList<>();
        for (Image image : product.getImages()) {
            list.add(new InputMediaPhoto(webhookPath + "/images/" + image.getId()));
        }
        sendMediaGroup.setChatId(chatId);
        sendMediaGroup.setMedias(list);
        list.get(list.size() - 1).setCaption(message);
        try {
            execute(sendMediaGroup);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private InlineKeyboardMarkup createInlineKeyboardMarkup(String url, String text) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        List<InlineKeyboardButton> list = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setUrl(url);
        list.add(inlineKeyboardButton);
        lists.add(list);
        inlineKeyboardMarkup.setKeyboard(lists);
        return inlineKeyboardMarkup;
    }

    private ReplyKeyboardMarkup createReplyKeyboardMarkup(String url, String text) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        WebAppInfo webAppInfo = new WebAppInfo(url);
        keyboardButton.setWebApp(webAppInfo);
        keyboardButton.setText(text);
        keyboardRow.add(keyboardButton);
        keyboardRows.add(keyboardRow);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

}
