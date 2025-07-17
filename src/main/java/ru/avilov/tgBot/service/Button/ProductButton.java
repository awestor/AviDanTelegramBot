package ru.avilov.tgBot.service.Button;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

public class ProductButton extends TelegramButton {
    private final Long productId;
    private final String name;
    private final double price;

    public ProductButton(Long productId, String name, double price) {
        this.productId = productId;
        this.name = name;
        this.price = price;
    }

    @Override
    public InlineKeyboardButton createButton() {
        return new InlineKeyboardButton(name + " — " + price + "₽")
                .callbackData("product:" + productId);
    }
}

