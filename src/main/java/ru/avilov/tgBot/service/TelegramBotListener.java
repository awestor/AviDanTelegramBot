package ru.avilov.tgBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Component
public class TelegramBotListener {

    private final TelegramBotServiceLogicImpl serviceLogic;

    @PostConstruct
    public void initBot() {
        String token = readEncodedToken();
        TelegramBot bot = new TelegramBot(token);
        BotRegistry.registerBot("mainBot", bot);

        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(java.util.List<Update> updates) {
                serviceLogic.handleUpdates(updates, bot);
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        });
    }

    public String readEncodedToken() {
        try {
            byte[] encodedBytes = Files.readAllBytes(Paths.get("encoded-token.txt"));
            byte[] decodedBytes = Base64.getDecoder().decode(encodedBytes);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении зашифрованного токена", e);
        }
    }

    public TelegramBotListener(TelegramBotServiceLogicImpl serviceLogic) {
        this.serviceLogic = serviceLogic;
    }

    // Данный класс использовался для шифрования. Он был оставлен для того чтобы можно
    // было проверить работоспособность кода для других токенов
    /*public void encodeTokenFile() {
        try {
            String token = Files.readString(Paths.get("token.txt")).trim();
            String encoded = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
            Files.write(Paths.get("encoded-token.txt"), encoded.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при шифровании токена", e);
        }
    }*/

}
