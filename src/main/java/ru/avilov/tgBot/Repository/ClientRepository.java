package ru.avilov.tgBot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.avilov.tgBot.Entity.Client;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "clients", path = "clients")
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Ищет клиентов, чьё полное имя содержит заданную подстроку,
     * без учёта регистра.
     *
     * @param pattern строка-подстрока для поиска в поле fullName
     * @return список клиентов, удовлетворяющих критерию
     */
    @Query("SELECT c FROM Client c WHERE LOWER(c.fullName) LIKE LOWER(:pattern)")
    List<Client> searchByNameContainsIgnoreCase(@Param("pattern") String pattern);

    /**
     * Проверяет наличие клиента по его внешнему идентификатору из Telegram.
     *
     * @param externalId Telegram ID пользователя
     * @return true, если клиент с таким ID существует
     */
    boolean existsByExternalId(Long externalId);

    /**
     * Находит клиента по его внешнему идентификатору из Telegram.
     *
     * @param externalId Telegram ID пользователя
     * @return Optional с найденным клиентом или пустой, если не найден
     */
    Optional<Client> findByExternalId(Long externalId);

}
