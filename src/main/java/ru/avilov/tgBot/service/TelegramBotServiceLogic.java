package ru.avilov.tgBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.List;

/**
 * Интерфейс бизнес-логики Telegram-бота.
 * Определяет обработчики входящих сообщений и callback-запросов,
 * а также является местом обращения к репозиториям.
 */
public interface TelegramBotServiceLogic {

    /**
     * Обрабатывает список входящих обновлений от Telegram.
     * В зависимости от типа обновления (текстовое сообщение или callback-запрос),
     * делегирует обработку соответствующим приватным методам.
     *
     * @param updates список входящих обновлений (сообщения, callback'и и др.)
     * @param bot TelegramBot-экземпляр, используемый для отправки ответов
     */
    void handleUpdates(List<Update> updates, TelegramBot bot);

}

