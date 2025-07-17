package ru.avilov.tgBot.service.Button;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

public class CountButton extends TelegramButton {
    private final Long productId;
    private final int quantity;

    public CountButton(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    @Override
    public InlineKeyboardButton createButton() {
        return new InlineKeyboardButton(String.valueOf(quantity))
                .callbackData("count:" + productId + ":" + quantity);
    }
}

