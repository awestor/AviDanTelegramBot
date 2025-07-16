package ru.avilov.tgBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;

/**
 * Интерфейс бизнес-логики Telegram-бота.
 * Определяет обработчики входящих сообщений и callback-запросов,
 * а также является местом обращения к репозиториям.
 */
public interface TelegramBotServiceLogic {

    /**
     * Обрабатывает входящее текстовое сообщение от пользователя Telegram.
     * Инициирует регистрацию пользователя, навигацию по категориям и обработку состояний.
     *
     * @param message входящее сообщение пользователя
     * @param bot TelegramBot-экземпляр, используемый для отправки ответов
     */
    void handleMessage(Message message, TelegramBot bot);

    /**
     * Обрабатывает callback-запросы, возникающие при взаимодействии пользователя
     * с inline-клавиатурами (кнопками).
     * В зависимости от типа запроса выполняет соответствующую навигацию или действие.
     *
     * @param callback callback-запрос от Telegram
     * @param bot TelegramBot-экземпляр, используемый для отправки ответов
     */
    void handleCallback(CallbackQuery callback, TelegramBot bot);
}

