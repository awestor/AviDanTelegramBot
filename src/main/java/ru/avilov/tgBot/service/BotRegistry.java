package ru.avilov.tgBot.service;

import com.pengrad.telegrambot.TelegramBot;

import java.util.HashMap;
import java.util.Map;

/*
 * Регистрирует и хранит всех используемых ботов для возможности получения их токена в любом месте приложения
 */
public class BotRegistry {

    private static final Map<String, TelegramBot> bots = new HashMap<>();

    public static void registerBot(String name, TelegramBot bot) {
        bots.put(name, bot);
    }

    public static TelegramBot getBot(String name) {
        return bots.get(name);
    }
}
