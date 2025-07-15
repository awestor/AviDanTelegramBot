package ru.avilov.tgBot.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.avilov.tgBot.Entity.ClientOrder;
import ru.avilov.tgBot.service.EntitiesService;

import java.util.List;

/**
 * REST-контроллер для обработки запросов, связанных с клиентскими заказами.
 * Позволяет получить список всех заказов, сделанных клиентом, по его идентификатору.
 * Роутинг: /rest/clients/{id}/orders
 */
@RestController
@RequestMapping("/rest/clients")
public class OrderRestController {

    private final EntitiesService entitiesService;

    public OrderRestController(EntitiesService entitiesService) {
        this.entitiesService = entitiesService;
    }

    /**
     * Получить все заказы, связанные с указанным клиентом.
     * Использует идентификатор клиента для поиска всех заказов, которые он выполнял.
     *
     * @param id идентификатор клиента
     * @return список заказов клиента
     */
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<ClientOrder>> getClientOrders(@PathVariable Long id) {
        return ResponseEntity.ok(entitiesService.getClientOrders(id));
    }
}

