package ru.avilov.tgBot.service;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ru.avilov.tgBot.service.Button.TelegramButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TelegramKeyboardBuilder {
    private final List<InlineKeyboardButton[]> rows = new ArrayList<>();

    /**
     * Добавляет кнопку в виде отдельной строки на клавиатуре.
     *
     * @param button экземпляр TelegramButton, реализующий логику формирования кнопки
     * @return текущий билдер для цепочки вызовов
     */
    public TelegramKeyboardBuilder addButton(TelegramButton button) {
        rows.add(new InlineKeyboardButton[]{button.createButton()});
        return this;
    }

    /**
     * Добавляет строку кнопок, расположенных горизонтально.
     *
     * @param buttons массив TelegramButton для одной строки
     * @return текущий билдер для цепочки вызовов
     */
    public TelegramKeyboardBuilder addRow(TelegramButton... buttons) {
        InlineKeyboardButton[] row = Arrays.stream(buttons)
                .map(TelegramButton::createButton)
                .toArray(InlineKeyboardButton[]::new);
        rows.add(row);
        return this;
    }

    /**
     * Формирует финальную клавиатуру Telegram, готовую к отправке.
     *
     * @return объект {@link InlineKeyboardMarkup} со всеми добавленными строками кнопок
     */
    public InlineKeyboardMarkup build() {
        return new InlineKeyboardMarkup(rows.toArray(new InlineKeyboardButton[0][]));
    }
}

