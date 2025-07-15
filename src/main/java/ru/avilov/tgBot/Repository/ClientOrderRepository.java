package ru.avilov.tgBot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.avilov.tgBot.Entity.ClientOrder;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "client-orders", path = "client-orders")
public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {

    @Query("SELECT clOrd FROM ClientOrder clOrd WHERE clOrd.client.id = :clientId")
    List<ClientOrder> findClientOrdersByClientId(@Param("clientId") Long clientId);

}
