package ru.avilov.tgBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class TelegramBotListenerImpl {

    private final TelegramBotServiceLogicImpl serviceLogic;

    public TelegramBotListenerImpl(TelegramBotServiceLogicImpl serviceLogic) {
        this.serviceLogic = serviceLogic;
    }

    @PostConstruct
    public void initBot() {
        String token = readTokenFromFile("token.txt");
        TelegramBot bot = new TelegramBot(token);

        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(java.util.List<Update> updates) {
                for (Update update : updates) {
                    if (update.callbackQuery() != null) {
                        serviceLogic.handleCallback(update.callbackQuery(), bot);
                    } else if (update.message() != null) {
                        serviceLogic.handleMessage(update.message(), bot);
                    }
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        });
    }

    private String readTokenFromFile(String fileName) {
        try {
            return Files.readString(Paths.get(fileName)).trim();
        } catch (IOException e) {
            throw new UncheckedIOException("Ошибка чтения токена из файла", e);
        }
    }
}
