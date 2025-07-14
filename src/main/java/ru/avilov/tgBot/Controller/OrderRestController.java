package ru.avilov.tgBot.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.avilov.tgBot.Entity.ClientOrder;
import ru.avilov.tgBot.service.EntitiesService;

import java.util.List;

@RestController
@RequestMapping("/rest/clients")
public class OrderRestController {

    private final EntitiesService entitiesService;

    public OrderRestController(EntitiesService entitiesService) {
        this.entitiesService = entitiesService;
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<ClientOrder>> getClientOrders(@PathVariable Long id) {
        return ResponseEntity.ok(entitiesService.getClientOrders(id));
    }
}

