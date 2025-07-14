package ru.avilov.tgBot.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.avilov.tgBot.Entity.Product;
import ru.avilov.tgBot.service.EntitiesService;

import java.util.List;

@RestController
@RequestMapping("/rest/products")
public class ProductRestController {

    private final EntitiesService entitiesService;

    public ProductRestController(EntitiesService entitiesService) {
        this.entitiesService = entitiesService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> getProductsByCategory(@RequestParam(required = false) Long categoryId,
                                                               @RequestParam(required = false) String name) {
        if (categoryId != null) {
            return ResponseEntity.ok(entitiesService.getProductsByCategoryId(categoryId));
        } else if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(entitiesService.searchProductsByName(name));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Product>> getPopularProducts(@RequestParam Integer limit) {
        return ResponseEntity.ok(entitiesService.getTopPopularProducts(limit));
    }
}
