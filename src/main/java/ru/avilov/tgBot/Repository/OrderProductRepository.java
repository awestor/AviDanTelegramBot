package ru.avilov.tgBot.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.avilov.tgBot.Entity.OrderProduct;
import ru.avilov.tgBot.Entity.Product;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "order-products", path = "order-products")
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    @Query("SELECT DISTINCT ordProd.product FROM OrderProduct ordProd WHERE ordProd.clientOrder.client.id = :clientId")
    List<Product> findClientProductsByClientId(@Param("clientId") Long clientId);

    @Query("SELECT oi.product FROM OrderProduct oi GROUP BY oi.product ORDER BY SUM(oi.countProduct) DESC")
    List<Product> findPopularProductsOrdered(Pageable pageable);

}
