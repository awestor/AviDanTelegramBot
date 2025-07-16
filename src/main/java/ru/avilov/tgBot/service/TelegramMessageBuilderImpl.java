package ru.avilov.tgBot.service;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.avilov.tgBot.Entity.Category;
import ru.avilov.tgBot.Entity.Product;
import ru.avilov.tgBot.Repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class TelegramMessageBuilderImpl implements TelegramMessageBuilder {

    private final ProductRepository productRepo;

    public TelegramMessageBuilderImpl(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public SendMessage buildCategoryKeyboard(Long chatId, String title, List<Category> categories) {
        InlineKeyboardButton[][] keyboard = categories.stream()
                .map(cat -> new InlineKeyboardButton[] {
                        new InlineKeyboardButton(cat.getName())
                                .callbackData("category:" + cat.getId())
                })
                .toArray(InlineKeyboardButton[][]::new);

        return new SendMessage(chatId, title).replyMarkup(new InlineKeyboardMarkup(keyboard));
    }

    @Override
    public SendMessage buildSubcategoryKeyboard(Long chatId, String title, List<Category> subcategories, Long categoryId) {
        List<InlineKeyboardButton[]> rows = subcategories.stream()
                .map(sub -> new InlineKeyboardButton[] {
                        new InlineKeyboardButton(sub.getName())
                                .callbackData("subcategory:" + sub.getId())
                })
                .collect(Collectors.toList());

        rows.add(new InlineKeyboardButton[] {
                new InlineKeyboardButton("<- Назад").callbackData("back:category"),
                new InlineKeyboardButton("Оформить заказ").callbackData("order")
        });

        InlineKeyboardButton[][] keyboard = rows.toArray(new InlineKeyboardButton[0][]);
        return new SendMessage(chatId, title).replyMarkup(new InlineKeyboardMarkup(keyboard));
    }

    @Override
    public SendMessage buildProductKeyboard(Long chatId, String title, List<Product> products, Long subcategoryId) {
        List<InlineKeyboardButton[]> rows = products.stream()
                .map(p -> new InlineKeyboardButton[] {
                        new InlineKeyboardButton(p.getName() + " — " + p.getPrice() + "₽")
                                .callbackData("product:" + p.getId())
                })
                .collect(Collectors.toList());

        rows.add(new InlineKeyboardButton[] {
                new InlineKeyboardButton("<- Назад").callbackData("back:subcategory:" + subcategoryId),
                new InlineKeyboardButton("Оформить заказ").callbackData("order")
        });

        InlineKeyboardButton[][] keyboard = rows.toArray(new InlineKeyboardButton[0][]);
        return new SendMessage(chatId, title).replyMarkup(new InlineKeyboardMarkup(keyboard));
    }

    @Override
    public SendMessage buildQuantitySelector(Long chatId, Long productId) {
        List<InlineKeyboardButton[]> rows = IntStream.rangeClosed(1, 5)
                .mapToObj(i -> new InlineKeyboardButton[] {
                        new InlineKeyboardButton(String.valueOf(i))
                                .callbackData("count:" + productId + ":" + i)
                })
                .collect(Collectors.toList());

        Long subcategoryId = productRepo.findById(productId)
                .map(p -> p.getCategory().getId())
                .orElse(0L);

        rows.add(new InlineKeyboardButton[] {
                new InlineKeyboardButton("<- Назад").callbackData("back:product:" + subcategoryId)
        });

        InlineKeyboardButton[][] keyboard = rows.toArray(new InlineKeyboardButton[0][]);
        return new SendMessage(chatId, "Сколько штук добавить?").replyMarkup(new InlineKeyboardMarkup(keyboard));
    }

    @Override
    public SendMessage buildConfirmSelector(Long chatId, Long productId, int count) {
        InlineKeyboardButton[][] keyboard = {
                {
                        new InlineKeyboardButton("Подтвердить")
                                .callbackData("confirm:" + productId + ":" + count),
                        new InlineKeyboardButton("<- Назад")
                                .callbackData("back:count:" + productId)
                }
        };

        return new SendMessage(chatId, "Добавить в заказ: " + count + " шт?").replyMarkup(new InlineKeyboardMarkup(keyboard));
    }
}
