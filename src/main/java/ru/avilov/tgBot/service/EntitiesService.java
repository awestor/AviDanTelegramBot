package ru.avilov.tgBot.service;

import ru.avilov.tgBot.Entity.Client;
import ru.avilov.tgBot.Entity.ClientOrder;
import ru.avilov.tgBot.Entity.Product;

import java.util.List;
public interface EntitiesService {

    /**
     * Получить список товаров в категории
     * @param id идентификатор категории
     */
    List<Product> getProductsByCategoryId(Long id);

    /**
     * Получить список заказов клиента
     * @param id идентификатор клиента
     */
    List<ClientOrder> getClientOrders(Long id);

    /**
     * Получить список всех товаров во всех заказах клиента
     * @param id идентификатор клиента
     */
    List<Product> getClientProducts(Long id);

    /**
     * Получить указанное кол-во самых популярных (наибольшее
     * количество штук в заказах) товаров среди клиентов
     * @param limit максимальное кол-во товаров
     */
    List<Product> getTopPopularProducts(Integer limit);

    /**
     * Найти всех клиентов по подстроке имени
     * @param name подстрока имени клиента
     */
    List<Client> searchClientsByName(String name);

    /**
     * Найти все продукты по подстроке названия
     * @param name подстрока названия продукта
     */
    List<Product> searchProductsByName(String name);

}
