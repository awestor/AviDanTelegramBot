package ru.avilov.tgBot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.avilov.tgBot.Entity.Category;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c.id FROM Category c WHERE c.parent.id = :parentId")
    List<Long> findChildIdsByParentId(@Param("parentId") Long parentId);

}
