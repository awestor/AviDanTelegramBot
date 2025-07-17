package ru.avilov.tgBot.service.Button;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

public abstract class TelegramButton {
    public abstract InlineKeyboardButton createButton();
}

