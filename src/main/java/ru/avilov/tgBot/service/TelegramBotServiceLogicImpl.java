package ru.avilov.tgBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
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
    private final TelegramMessageCreatorImpl messageBuilder;

    private enum RegistrationState { NONE, WAIT_PHONE, WAIT_ADDRESS }

    private final Map<Long, RegistrationState> registrationMap = new HashMap<>();
    private final Map<Long, String> phoneBuffer = new HashMap<>();

    public TelegramBotServiceLogicImpl(CategoryRepository categoryRepo,
                                       ProductRepository productRepo,
                                       ClientRepository clientRepo,
                                       ClientOrderRepository clientOrderRepo,
                                       OrderProductRepository orderProductRepo,
                                       TelegramMessageCreatorImpl messageBuilder) {
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.clientRepo = clientRepo;
        this.clientOrderRepo = clientOrderRepo;
        this.orderProductRepo = orderProductRepo;
        this.messageBuilder = messageBuilder;
    }

    @Override
    public void handleUpdates(List<Update> updates, TelegramBot bot) {
        for (Update update : updates) {
            if (update.callbackQuery() != null) {
                handleCallback(update.callbackQuery(), bot);
            } else if (update.message() != null) {
                handleMessage(update.message(), bot);
            }
        }
    }

    private void handleMessage(Message message, TelegramBot bot) {
        Long chatId = message.chat().id();
        String text = message.text();

        RegistrationState state = registrationMap.get(chatId);

        if (state == null && clientRepo.existsByExternalId(chatId)) {
            Client client = clientRepo.findByExternalId(chatId).orElseThrow();

            if ("unknown".equals(client.getPhoneNumber())) {
                registrationMap.put(chatId, RegistrationState.WAIT_PHONE);
                bot.execute(new SendMessage(chatId, "Продолжим регистрацию. Введите номер телефона:"));
                return;
            } else if ("unknown".equals(client.getAddress())) {
                registrationMap.put(chatId, RegistrationState.WAIT_ADDRESS);
                bot.execute(new SendMessage(chatId, "Укажите адрес доставки:"));
                return;
            }
        }

        if (!clientRepo.existsByExternalId(chatId) && state == null) {
            createClientAndOrder(chatId, message.chat());
            registrationMap.put(chatId, RegistrationState.WAIT_PHONE);
            bot.execute(new SendMessage(chatId, "Добро пожаловать! Введите номер телефона:"));
            return;
        }

        switch (registrationMap.getOrDefault(chatId, RegistrationState.NONE)) {
            case WAIT_PHONE -> processPhoneInput(chatId, text, bot);
            case WAIT_ADDRESS -> processAddressInput(chatId, text, bot);
            default -> sendCategoryKeyboard(chatId, bot);
        }
    }


    @Transactional
    private void createClientAndOrder(Long chatId, Chat chat) {
        String fullName = chat.firstName() + " " + chat.lastName();

        Client client = new Client();
        client.setExternalId(chatId);
        client.setFullName(fullName);
        client.setPhoneNumber("unknown");
        client.setAddress("unknown");

        ClientOrder order = new ClientOrder();
        order.setClient(client);
        order.setStatus(1);
        order.setTotal(0.0);

        clientRepo.save(client);
        clientOrderRepo.save(order);
    }

    private void processPhoneInput(Long chatId, String phone, TelegramBot bot) {
        Client client = clientRepo.findByExternalId(chatId).orElseThrow();
        client.setPhoneNumber(phone);
        clientRepo.save(client);

        registrationMap.put(chatId, RegistrationState.WAIT_ADDRESS);
        bot.execute(new SendMessage(chatId, "Теперь укажите адрес доставки:"));
    }

    private void processAddressInput(Long chatId, String address, TelegramBot bot) {
        Client client = clientRepo.findByExternalId(chatId).orElseThrow();
        client.setAddress(address);
        clientRepo.save(client);

        registrationMap.remove(chatId);
        sendCategoryKeyboard(chatId, bot);
    }

    private void sendCategoryKeyboard(Long chatId, TelegramBot bot) {
        List<Category> rootCategories = categoryRepo.findByParentIsNullOrderById();
        bot.execute(messageBuilder.buildCategoryKeyboard(chatId, "Категории:", rootCategories));
    }


    @Transactional
    private void handleCallback(CallbackQuery callback, TelegramBot bot) {
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
