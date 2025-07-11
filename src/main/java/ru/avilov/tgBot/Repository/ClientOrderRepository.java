package ru.avilov.tgBot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.avilov.tgBot.Entity.ClientOrder;

@RepositoryRestResource(collectionResourceRel = "client-orders", path = "client-orders")
public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
}
