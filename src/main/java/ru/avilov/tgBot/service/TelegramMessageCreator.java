package ru.avilov.tgBot.service;

import com.pengrad.telegrambot.request.SendMessage;
import ru.avilov.tgBot.Entity.Category;
import ru.avilov.tgBot.Entity.Product;

import java.util.List;

/**
 * TelegramMessageBuilder содержит методы
 * для генерации сообщений Telegram с клавиатурами.
 * Используется для отображения навигации и подтверждений действий пользователя.
 */
public interface TelegramMessageCreator {

    /**
     * Строит клавиатуру с корневыми категориями.
     *
     * @param chatId ID чата Telegram
     * @param title Заголовок сообщения
     * @param categories Список корневых категорий
     * @return сообщение с inline-клавиатурой
     */
    SendMessage buildCategoryKeyboard(Long chatId, String title, List<Category> categories);

    /**
     * Строит клавиатуру подкатегорий, включая кнопки “назад” и “оформить заказ”.
     *
     * @param chatId ID чата Telegram
     * @param title Заголовок сообщения
     * @param subcategories Список подкатегорий
     * @param categoryId ID родительской категории (для “назад”)
     * @return сообщение с inline-клавиатурой
     */
    SendMessage buildSubcategoryKeyboard(Long chatId, String title, List<Category> subcategories, Long categoryId);

    /**
     * Строит клавиатуру выбора продукта, включая цены и кнопки “назад” и “оформить заказ”.
     *
     * @param chatId ID чата Telegram
     * @param title Заголовок сообщения
     * @param products Список продуктов выбранной подкатегории
     * @param subcategoryId ID подкатегории (для “назад”)
     * @return сообщение с inline-клавиатурой
     */
    SendMessage buildProductKeyboard(Long chatId, String title, List<Product> products, Long subcategoryId);

    /**
     * Строит клавиатуру для выбора количества продукта.
     *
     * @param chatId ID чата Telegram
     * @param productId ID выбранного продукта
     * @return сообщение с inline-клавиатурой для выбора количества
     */
    SendMessage buildQuantitySelector(Long chatId, Long productId);

    /**
     * Строит клавиатуру для подтверждения добавления продукта в заказ.
     *
     * @param chatId ID чата Telegram
     * @param productId ID продукта
     * @param count Количество товара
     * @return сообщение с inline-клавиатурой “подтвердить” и “назад”
     */
    SendMessage buildConfirmSelector(Long chatId, Long productId, int count);
}

