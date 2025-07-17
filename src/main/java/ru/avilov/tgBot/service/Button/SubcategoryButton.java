package ru.avilov.tgBot.service.Button;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

public class SubcategoryButton extends TelegramButton {
    private final String name;
    private final Long subcategoryId;

    public SubcategoryButton(String name, Long subcategoryId) {
        this.name = name;
        this.subcategoryId = subcategoryId;
    }

    @Override
    public InlineKeyboardButton createButton() {
        return new InlineKeyboardButton(name).callbackData("subcategory:" + subcategoryId);
    }
}

