package ru.avilov.tgBot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.avilov.tgBot.Entity.Category;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNullOrderById();

    List<Category> findByParentIdOrderById(Long parentId);

    /**
     * Возвращает категорию по ID, но может и вернуть Optional.empty()!!!
     */
    Optional<Category> findById(Long id);

    /**
     * Возвращает все подкатегории, от родителя подкатегории (подкатегории одного уровня)
     */
    @Query("SELECT c2 FROM Category c1 JOIN Category c2 ON c2.parent.id = c1.parent.id WHERE c1.id = :subcategoryId")
    List<Category> findSiblingSubcategories(@Param("subcategoryId") Long subcategoryId);


}
