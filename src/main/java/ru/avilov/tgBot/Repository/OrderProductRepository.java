package ru.avilov.tgBot.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.avilov.tgBot.Entity.ClientOrder;
import ru.avilov.tgBot.Entity.OrderProduct;
import ru.avilov.tgBot.Entity.Product;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "order-products", path = "order-products")
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    /**
     * Возвращает список всех уникальных продуктов, добавленных в заказы конкретного клиента.
     *
     * @param clientId внутренний идентификатор клиента
     * @return список уникальных товаров, заказанных данным клиентом
     */
    @Query("SELECT DISTINCT ordProd.product FROM OrderProduct ordProd WHERE ordProd.clientOrder.client.id = :clientId")
    List<Product> findClientProductsByClientId(@Param("clientId") Long clientId);

    /**
     * Возвращает наиболее популярные товары по количеству добавлений в заказы.
     *
     * @param pageable настройки пагинации и ограничения количества записей
     * @return список популярных товаров, отсортированных по убыванию популярности
     */
    @Query("SELECT oi.product FROM OrderProduct oi GROUP BY oi.product ORDER BY SUM(oi.countProduct) DESC")
    List<Product> findPopularProductsOrdered(Pageable pageable);

    /**
     * Проверяет, существует ли запись товара в указанном заказе клиента.
     *
     * @param order  заказ клиента
     * @param product продукт, который проверяется
     * @return true, если продукт уже добавлен в заказ
     */
    boolean existsByClientOrderAndProduct(ClientOrder order, Product product);

    /**
     * Возвращает все записи связи товаров с указанным заказом клиента.
     *
     * @param order заказ клиента
     * @return список позиций (OrderProduct), включённых в заказ
     */
    List<OrderProduct> findByClientOrder(ClientOrder order);

    /**
     * Удаляет все товарные позиции, связанные с указанным заказом клиента.
     *
     * @param order заказ клиента, из которого следует удалить все продукты
     */
    @Modifying
    @Query("DELETE FROM OrderProduct op WHERE op.clientOrder = :order")
    void deleteByClientOrder(@Param("order") ClientOrder order);

}
