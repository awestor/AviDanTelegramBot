package ru.avilov.tgBot.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.avilov.tgBot.Entity.Client;
import ru.avilov.tgBot.Entity.Product;
import ru.avilov.tgBot.service.EntitiesService;

import java.util.List;

@RestController
@RequestMapping("/rest/clients")
public class ClientRestController {

    private final EntitiesService entitiesService;

    public ClientRestController(EntitiesService entitiesService) {
        this.entitiesService = entitiesService;
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<Product>> getClientOrderedProducts(@PathVariable Long id) {
        System.out.println("Output data:" + entitiesService.getClientProducts(id));
        return ResponseEntity.ok(entitiesService.getClientProducts(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Client>> searchClientsByName(@RequestParam String name) {
        return ResponseEntity.ok(entitiesService.searchClientsByName(name));
    }
}

