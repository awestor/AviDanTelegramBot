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
    /**
     * Возвращает список корневых категорий (у которых нет родителя),
     * отсортированных по идентификатору.
     *
     * @return список категорий верхнего уровня
     */
    List<Category> findByParentIsNullOrderById();

    /**
     * Возвращает список подкатегорий, у которых родительская категория имеет заданный ID.
     * Результат отсортирован по идентификатору.
     *
     * @param parentId идентификатор родительской категории
     * @return список дочерних категорий
     */
    List<Category> findByParentIdOrderById(Long parentId);

    /**
     * Находит категорию по её идентификатору.
     *
     * @param id идентификатор категории
     * @return {@link Optional} содержащий найденную категорию или пустой результат
     */
    Optional<Category> findById(Long id);

    /**
     * Находит «соседние» подкатегории — категории, имеющие такого же родителя,
     * как и указанная категория.
     *
     * @param subcategoryId идентификатор существующей подкатегории
     * @return список подкатегорий с тем же родителем
     */
    @Query("SELECT c2 FROM Category c1 JOIN Category c2 ON c2.parent.id = c1.parent.id WHERE c1.id = :subcategoryId")
    List<Category> findSiblingSubcategories(@Param("subcategoryId") Long subcategoryId);

}
