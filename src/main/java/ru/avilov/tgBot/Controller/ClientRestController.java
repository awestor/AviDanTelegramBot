package ru.avilov.tgBot.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.avilov.tgBot.Entity.Client;
import ru.avilov.tgBot.Entity.Product;
import ru.avilov.tgBot.service.EntitiesService;

import java.util.List;

/**
 * REST-контроллер для операций, связанных с клиентами.
 * Обрабатывает запросы на поиск клиентов по имени и получение списка товаров, приобретённых клиентом.
 * Роутинг: /rest/clients
 */
@RestController
@RequestMapping("/rest/clients")
public class ClientRestController {

    private final EntitiesService entitiesService;

    public ClientRestController(EntitiesService entitiesService) {
        this.entitiesService = entitiesService;
    }

    /**
     * Получить список всех товаров, когда-либо заказанных указанным клиентом.
     * Метод извлекает товары, связанные с заказами клиента по его идентификатору.
     *
     * @param id идентификатор клиента
     * @return список продуктов, приобретённых этим клиентом
     */
    @GetMapping("/{id}/products")
    public ResponseEntity<List<Product>> getClientOrderedProducts(@PathVariable Long id) {
        System.out.println("Output data:" + entitiesService.getClientProducts(id));
        return ResponseEntity.ok(entitiesService.getClientProducts(id));
    }

    /**
     * Поиск клиентов по фрагменту имени.
     * Возвращает всех клиентов, чьё полное имя содержит переданную строку без учёта регистра.
     *
     * @param name подстрока имени для поиска
     * @return список подходящих клиентов
     */
    @GetMapping("/search")
    public ResponseEntity<List<Client>> searchClientsByName(@RequestParam String name) {
        return ResponseEntity.ok(entitiesService.searchClientsByName(name));
    }
}

