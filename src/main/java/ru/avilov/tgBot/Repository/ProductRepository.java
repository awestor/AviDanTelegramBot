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

    @Query("SELECT prod FROM Product prod JOIN FETCH prod.category WHERE prod.category.id = :categoryId")
    List<Product> findProductsByCategoryId(@Param("categoryId") Long categoryId);


    @Query("SELECT prod FROM Product prod WHERE LOWER(prod.name) LIKE LOWER(:pattern)")
    List<Product> findByNameLikeIgnoreCase(@Param("pattern") String pattern);

    List<Product> findByCategoryIdOrderById(Long categoryId);

    /**
     * Возвращает продукт по ID, но может и вернуть Optional.empty()!!!
     */
    Optional<Product> findById(Long id);

}
