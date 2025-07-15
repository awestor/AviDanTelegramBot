package ru.avilov.tgBot.service;

import ru.avilov.tgBot.Entity.Client;
import ru.avilov.tgBot.Entity.ClientOrder;
import ru.avilov.tgBot.Entity.Product;

import java.util.List;
public interface EntitiesService {

    /**
     * Получить список товаров в категории
     * @param id идентификатор категории
     * @return список сущностей Product, относящихся к категории
     */
    List<Product> getProductsByCategoryId(Long id);

    /**
     * Получить список заказов клиента
     * @param id идентификатор клиента
     * @return список сущностей ClientOrder, относящихся к клиенту
     */
    List<ClientOrder> getClientOrders(Long id);

    /**
     * Получить список всех товаров во всех заказах клиента
     * @param id идентификатор клиента
     * @return список сущностей Product, что были заказаны пользователем когда-либо
     */
    List<Product> getClientProducts(Long id);

    /**
     * Получить указанное кол-во самых популярных (наибольшее
     * количество штук в заказах) товаров среди клиентов
     * @param limit максимальное кол-во товаров
     * @return список сущностей Product, что были заказаны наибольшее количество раз
     */
    List<Product> getTopPopularProducts(Integer limit);

    /**
     * Найти всех клиентов по подстроке имени
     * @param name подстрока имени клиента
     * @return список сущностей Client, что содержат указанные в параметре символы в поле fullname
     */
    List<Client> searchClientsByName(String name);

    /**
     * Найти все продукты по подстроке названия
     * @param name подстрока названия продукта
     * @return список сущностей Product, что содержат указанные в параметре символы в поле name
     */
    List<Product> searchProductsByName(String name);

}
