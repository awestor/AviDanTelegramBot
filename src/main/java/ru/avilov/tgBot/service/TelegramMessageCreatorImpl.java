package ru.avilov.tgBot.service;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.avilov.tgBot.Entity.Category;
import ru.avilov.tgBot.Entity.Product;
import ru.avilov.tgBot.Repository.ProductRepository;
import ru.avilov.tgBot.service.Button.*;

import java.util.List;
import java.util.stream.IntStream;

@Component
public class TelegramMessageCreatorImpl implements TelegramMessageCreator {

    private final ProductRepository productRepo;

    public TelegramMessageCreatorImpl(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public SendMessage buildCategoryKeyboard(Long chatId, String title, List<Category> categories) {
        TelegramKeyboardCreator builder = new TelegramKeyboardCreator();
        categories.forEach(cat -> builder.addButton(new CategoryButton(cat.getName(), cat.getId())));
        return createTextMessageWithKeyboard(chatId, title, builder.build());
    }

    @Override
    public SendMessage buildSubcategoryKeyboard(Long chatId, String title, List<Category> subcategories, Long categoryId) {
        TelegramKeyboardCreator builder = new TelegramKeyboardCreator();
        subcategories.forEach(sub -> builder.addButton(new SubcategoryButton(sub.getName(), sub.getId())));
        builder.addRow(new BackButton("category"), new OrderButton());
        return createTextMessageWithKeyboard(chatId, title, builder.build());
    }

    @Override
    public SendMessage buildProductKeyboard(Long chatId, String title, List<Product> products, Long subcategoryId) {
        TelegramKeyboardCreator builder = new TelegramKeyboardCreator();
        products.forEach(p -> builder.addButton(new ProductButton(p.getId(), p.getName(), p.getPrice())));
        builder.addRow(new BackButton("subcategory:" + subcategoryId), new OrderButton());
        return createTextMessageWithKeyboard(chatId, title, builder.build());
    }

    @Override
    public SendMessage buildQuantitySelector(Long chatId, Long productId) {
        TelegramKeyboardCreator builder = new TelegramKeyboardCreator();
        IntStream.rangeClosed(1, 5).forEach(i -> builder.addButton(new CountButton(productId, i)));

        Long subcategoryId = productRepo.findById(productId)
                .map(p -> p.getCategory().getId())
                .orElse(0L);

        builder.addButton(new BackButton("product:" + subcategoryId));
        return createTextMessageWithKeyboard(chatId, "Сколько штук добавить?", builder.build());
    }

    @Override
    public SendMessage buildConfirmSelector(Long chatId, Long productId, int count) {
        TelegramKeyboardCreator builder = new TelegramKeyboardCreator();
        builder.addRow(
                new ConfirmButton(productId, count),
                new BackButton("count:" + productId)
        );
        return createTextMessageWithKeyboard(chatId, "Добавить в заказ: " + count + " шт?", builder.build());
    }

    private SendMessage createTextMessageWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        return new SendMessage(chatId, text).replyMarkup(keyboard);
    }
}
