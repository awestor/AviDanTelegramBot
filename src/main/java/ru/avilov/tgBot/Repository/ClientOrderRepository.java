package ru.avilov.tgBot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.avilov.tgBot.Entity.ClientOrder;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "client-orders", path = "client-orders")
public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {

    @Query("SELECT clOrd FROM ClientOrder clOrd WHERE clOrd.client.id = :clientId")
    List<ClientOrder> findClientOrdersByClientId(@Param("clientId") Long clientId);

    @Query("SELECT o FROM ClientOrder o WHERE o.client.externalId = :externalId")
    Optional<ClientOrder> findByClientExternalId(@Param("externalId") Long externalId);

    @Query("SELECT o FROM ClientOrder o WHERE o.client.externalId = :externalId AND o.status = 1")
    Optional<ClientOrder> findActiveDraftByClientExternalId(@Param("externalId") Long externalId);


}
