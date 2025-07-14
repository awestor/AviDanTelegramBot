package ru.avilov.tgBot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.avilov.tgBot.Entity.Product;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "products", path = "products")
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT prod FROM Product prod JOIN FETCH prod.category WHERE prod.category.id IN :categoryIds")
    List<Product> findProductsByCategoryIds(@Param("categoryIds") List<Long> categoryIds);


    @Query("SELECT prod FROM Product prod WHERE LOWER(prod.name) LIKE :pattern")
    List<Product> findByNameLikeIgnoreCase(@Param("pattern") String pattern);

}
