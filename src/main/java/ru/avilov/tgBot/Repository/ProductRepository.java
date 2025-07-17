package ru.avilov.tgBot.Repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.avilov.tgBot.Entity.Product;

import java.util.List;
import java.util.Optional;


@RepositoryRestResource(collectionResourceRel = "products", path = "products")
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Возвращает список товаров, принадлежащих указанной категории.
     *
     * @param categoryId идентификатор категории, к которой относятся товары
     * @return список товаров с загруженной категорией
     */
    @Query("SELECT prod FROM Product prod JOIN FETCH prod.category WHERE prod.category.id = :categoryId")
    List<Product> findProductsByCategoryId(@Param("categoryId") Long categoryId);


    /**
     * Ищет товары, имя которых содержит заданную подстроку (без учёта регистра).
     *
     * @param pattern текстовый шаблон
     * @return список совпадающих товаров
     */
    @Query("SELECT prod FROM Product prod WHERE LOWER(prod.name) LIKE LOWER(:pattern)")
    List<Product> findByNameLikeIgnoreCase(@Param("pattern") String pattern);

    /**
     * Получает товары, относящиеся к заданной категории, отсортированные по ID.
     *
     * @param categoryId идентификатор категории
     * @return отсортированный список товаров
     */
    List<Product> findByCategoryIdOrderById(Long categoryId);

    /**
     * Находит товар по его идентификатору.
     *
     * @param id уникальный идентификатор товара
     * @return {@link Optional} с найденным товаром или пустой, если не найден
     */
    Optional<Product> findById(Long id);

}
