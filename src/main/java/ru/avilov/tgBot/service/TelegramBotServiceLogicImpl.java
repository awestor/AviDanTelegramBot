package ru.avilov.tgBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.avilov.tgBot.Entity.*;
import ru.avilov.tgBot.Repository.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TelegramBotServiceLogicImpl implements TelegramBotServiceLogic {

    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;
    private final ClientRepository clientRepo;
    private final ClientOrderRepository clientOrderRepo;
    private final OrderProductRepository orderProductRepo;
    private final TelegramMessageBuilderImpl messageBuilder;

    private enum RegistrationState { NONE, WAIT_PHONE, WAIT_ADDRESS }

    private final Map<Long, RegistrationState> registrationMap = new HashMap<>();
    private final Map<Long, String> phoneBuffer = new HashMap<>();

    public TelegramBotServiceLogicImpl(CategoryRepository categoryRepo,
                                       ProductRepository productRepo,
                                       ClientRepository clientRepo,
                                       ClientOrderRepository clientOrderRepo,
                                       OrderProductRepository orderProductRepo,
                                       TelegramMessageBuilderImpl messageBuilder) {
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.clientRepo = clientRepo;
        this.clientOrderRepo = clientOrderRepo;
        this.orderProductRepo = orderProductRepo;
        this.messageBuilder = messageBuilder;
    }

    @Override
    public void handleMessage(Message message, TelegramBot bot) {
        Long chatId = message.chat().id();
        Chat chat = message.chat();
        String text = message.text();
        RegistrationState state = registrationMap.getOrDefault(chatId, RegistrationState.NONE);

        if (!clientRepo.existsByExternalId(chatId) && state == RegistrationState.NONE) {
            registrationMap.put(chatId, RegistrationState.WAIT_PHONE);
            bot.execute(new SendMessage(chatId, "Добро пожаловать! Введите номер телефона:"));
            return;
        }

        switch (state) {
            case WAIT_PHONE -> {
                phoneBuffer.put(chatId, text);
                registrationMap.put(chatId, RegistrationState.WAIT_ADDRESS);
                bot.execute(new SendMessage(chatId, "Теперь укажите адрес доставки:"));
            }
            case WAIT_ADDRESS -> {
                String phone = phoneBuffer.get(chatId);
                String address = text;
                String fullName = chat.firstName() + " " + chat.lastName();
                String username = chat.username() != null ? chat.username() : "unknown";

                Client client = new Client();
                client.setExternalId(chatId);
                client.setFullName(fullName);
                client.setPhoneNumber(phone);
                client.setAddress(address);

                Client savedClient = clientRepo.save(client);

                ClientOrder order = new ClientOrder();
                order.setClient(savedClient);
                order.setStatus(1);
                order.setTotal(0.0);
                clientOrderRepo.save(order);

                registrationMap.remove(chatId);
                phoneBuffer.remove(chatId);

                List<Category> rootCategories = categoryRepo.findByParentIsNullOrderById();
                bot.execute(messageBuilder.buildCategoryKeyboard(chatId, "Категории:", rootCategories));
            }
            default -> {
                List<Category> rootCategories = categoryRepo.findByParentIsNullOrderById();
                bot.execute(messageBuilder.buildCategoryKeyboard(chatId, "Категории:", rootCategories));
            }
        }
    }

    @Override
    @Transactional
    public void handleCallback(CallbackQuery callback, TelegramBot bot) {
        Long chatId = callback.message().chat().id();
        String data = callback.data();

        if (!clientRepo.existsByExternalId(chatId)) {
            bot.execute(new SendMessage(chatId, "Пожалуйста, начните с регистрации — отправьте сообщение."));
            return;
        }

        switch (resolveCallback(data)) {
            case "category" -> handleCategory(data, chatId, bot);
            case "subcategory" -> handleSubcategory(data, chatId, bot);
            case "product" -> handleProduct(data, chatId, bot);
            case "count" -> handleCount(data, chatId, bot);
            case "confirm" -> handleConfirm(data, chatId, bot);
            case "order" -> handleOrderReview(chatId, bot);
            case "order_clear" -> handleOrderClear(chatId, bot);
            case "order_confirm" -> handleOrderConfirm(chatId, bot);
            case "back" -> handleBackNavigation(data, chatId, bot);
        }
    }

    private void handleBackNavigation(String data, Long chatId, TelegramBot bot) {
        String[] parts = data.split(":");

        switch (parts[1]) {
            case "category" -> {
                List<Category> roots = categoryRepo.findByParentIsNullOrderById();
                bot.execute(messageBuilder.buildCategoryKeyboard(chatId, "Категории:", roots));
            }
            case "subcategory" -> {
                Long subcategoryId = Long.parseLong(parts[2]);
                List<Category> subcategories = categoryRepo.findSiblingSubcategories(subcategoryId);
                bot.execute(messageBuilder.buildSubcategoryKeyboard(chatId, "Подкатегории:", subcategories, subcategoryId));
            }
            case "product" -> {
                Long subcategoryId = Long.parseLong(parts[2]);
                List<Product> products = productRepo.findByCategoryIdOrderById(subcategoryId);
                bot.execute(messageBuilder.buildProductKeyboard(chatId, "Продукты:", products, subcategoryId));
            }
            case "count" -> {
                Long productId = Long.parseLong(parts[2]);
                bot.execute(messageBuilder.buildQuantitySelector(chatId, productId));
            }
        }
    }


    private void handleCategory(String data, Long chatId, TelegramBot bot) {
        Long categoryId = Long.parseLong(data.split(":")[1]);
        List<Category> subcategories = categoryRepo.findByParentIdOrderById(categoryId);
        bot.execute(messageBuilder.buildSubcategoryKeyboard(chatId, "Подкатегории:", subcategories, categoryId));
    }

    private void handleSubcategory(String data, Long chatId, TelegramBot bot) {
        Long subcategoryId = Long.parseLong(data.split(":")[1]);
        List<Product> products = productRepo.findByCategoryIdOrderById(subcategoryId);
        bot.execute(messageBuilder.buildProductKeyboard(chatId, "Продукты:", products, subcategoryId));
    }

    private void handleProduct(String data, Long chatId, TelegramBot bot) {
        Long productId = Long.parseLong(data.split(":")[1]);
        bot.execute(messageBuilder.buildQuantitySelector(chatId, productId));
    }

    private void handleCount(String data, Long chatId, TelegramBot bot) {
        String[] parts = data.split(":");
        Long productId = Long.parseLong(parts[1]);
        int count = Integer.parseInt(parts[2]);
        bot.execute(messageBuilder.buildConfirmSelector(chatId, productId, count));
    }

    private void handleConfirm(String data, Long chatId, TelegramBot bot) {
        String[] parts = data.split(":");
        Long productId = Long.parseLong(parts[1]);
        int count = Integer.parseInt(parts[2]);

        Product product = productRepo.findById(productId).orElseThrow();
        ClientOrder order = clientOrderRepo.findActiveDraftByClientExternalId(chatId).orElseThrow();


        if (!orderProductRepo.existsByClientOrderAndProduct(order, product)) {
            OrderProduct op = new OrderProduct();
            op.setClientOrder(order);
            op.setProduct(product);
            op.setCountProduct(count);
            orderProductRepo.save(op);

            order.setTotal(order.getTotal() + product.getPrice() * count);
            clientOrderRepo.save(order);
        }

        bot.execute(new SendMessage(chatId, "Добавлено: " + product.getName() + " × " + count));
    }

    private void handleOrderReview(Long chatId, TelegramBot bot) {
        ClientOrder order = clientOrderRepo.findActiveDraftByClientExternalId(chatId).orElseThrow();

        List<OrderProduct> items = orderProductRepo.findByClientOrder(order);

        if (items.isEmpty()) {
            bot.execute(new SendMessage(chatId, "Корзина пуста."));
            return;
        }

        StringBuilder sb = new StringBuilder("Ваш заказ:\n\n");
        for (OrderProduct item : items) {
            sb.append("• ").append(item.getProduct().getName())
                    .append(" × ").append(item.getCountProduct())
                    .append(" = ").append(item.getProduct().getPrice() * item.getCountProduct())
                    .append("₽\n");
        }
        sb.append("\nИтого: ").append(order.getTotal()).append("₽");

        InlineKeyboardButton[][] keyboard = {
                {
                        new InlineKeyboardButton("Очистить").callbackData("order_clear"),
                        new InlineKeyboardButton("Подтвердить").callbackData("order_confirm")
                }
        };

        bot.execute(new SendMessage(chatId, sb.toString()).replyMarkup(new InlineKeyboardMarkup(keyboard)));
    }

    private void handleOrderClear(Long chatId, TelegramBot bot) {
        ClientOrder order = clientOrderRepo.findActiveDraftByClientExternalId(chatId).orElseThrow();

        List<OrderProduct> items = orderProductRepo.findByClientOrder(order);
        orderProductRepo.deleteAll(items);
        order.setTotal(0.0);
        clientOrderRepo.save(order);

        bot.execute(new SendMessage(chatId, "Заказ очищен."));
    }

    private void handleOrderConfirm(Long chatId, TelegramBot bot) {
        ClientOrder order = clientOrderRepo.findActiveDraftByClientExternalId(chatId).orElseThrow();

        List<OrderProduct> items = orderProductRepo.findByClientOrder(order);

        if (items.isEmpty()) {
            bot.execute(new SendMessage(chatId, "Корзина пуста. Добавьте товары перед подтверждением."));
            return;
        }

        order.setStatus(2); // заказ подтверждён
        clientOrderRepo.save(order);

        StringBuilder sb = new StringBuilder("Заказ подтверждён!\n\n");
        for (OrderProduct item : items) {
            sb.append("• ").append(item.getProduct().getName())
                    .append(" × ").append(item.getCountProduct())
                    .append(" = ").append(item.getProduct().getPrice() * item.getCountProduct())
                    .append("₽\n");
        }
        sb.append("\nОбщая сумма: ").append(order.getTotal()).append("₽\n");
        sb.append("Адрес доставки: ").append(order.getClient().getAddress());

        bot.execute(new SendMessage(chatId, sb.toString()));

        // Создание нового заказа
        ClientOrder newOrder = new ClientOrder();
        newOrder.setClient(order.getClient());
        newOrder.setStatus(1);
        newOrder.setTotal(0.0);
        clientOrderRepo.save(newOrder);
    }

    private String resolveCallback(String data) {
        return data.contains(":") ? data.split(":")[0] : data;
    }
}
