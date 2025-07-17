package ru.avilov.tgBot.service.Button;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

public class OrderButton extends TelegramButton {
    @Override
    public InlineKeyboardButton createButton() {
        return new InlineKeyboardButton("Оформить заказ").callbackData("order");
    }
}
