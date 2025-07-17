package ru.avilov.tgBot.service.Button;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

public class CategoryButton extends TelegramButton {
    private final String name;
    private final Long categoryId;

    public CategoryButton(String name, Long categoryId) {
        this.name = name;
        this.categoryId = categoryId;
    }

    @Override
    public InlineKeyboardButton createButton() {
        return new InlineKeyboardButton(name).callbackData("category:" + categoryId);
    }
}

