package ru.avilov.tgBot.service.Button;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

public class BackButton extends TelegramButton {
    private final String target;

    public BackButton(String target) {
        this.target = target;
    }

    @Override
    public InlineKeyboardButton createButton() {
        return new InlineKeyboardButton("<- Назад").callbackData("back:" + target);
    }
}

