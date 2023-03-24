package bot.telegram.controller;

import bot.telegram.parsers.Parser;
import bot.telegram.repositories.ProductRepository;
import bot.telegram.service.TelegramBot;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@AllArgsConstructor
public class WebHookController {
    private final TelegramBot telegramBot;
    private final ProductRepository productRepository;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        productRepository.save(Parser.getInstance(update.getMessage().getText()).parse());
        return telegramBot.onWebhookUpdateReceived(update);
    }

}
