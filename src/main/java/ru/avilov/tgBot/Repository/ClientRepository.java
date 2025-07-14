package ru.avilov.tgBot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.avilov.tgBot.Entity.Client;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "clients", path = "clients")
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c WHERE LOWER(c.fullName) LIKE :pattern")
    List<Client> searchByNameContainsIgnoreCase(@Param("pattern") String pattern);

}
