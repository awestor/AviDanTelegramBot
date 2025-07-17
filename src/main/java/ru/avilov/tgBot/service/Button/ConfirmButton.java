package ru.avilov.tgBot.service.Button;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

public class ConfirmButton extends TelegramButton {
    private final Long productId;
    private final int quantity;

    public ConfirmButton(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    @Override
    public InlineKeyboardButton createButton() {
        return new InlineKeyboardButton("Подтвердить")
                .callbackData("confirm:" + productId + ":" + quantity);
    }
}

