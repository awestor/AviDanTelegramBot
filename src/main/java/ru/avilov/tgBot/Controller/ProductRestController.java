package ru.avilov.tgBot.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.avilov.tgBot.Entity.Product;
import ru.avilov.tgBot.service.EntitiesService;

import java.util.List;

/**
 * REST-контроллер для работы с продуктами.
 * Предоставляет эндпоинты для поиска продуктов по имени или категории,
 * а также для получения списка наиболее популярных товаров по количеству заказов.
 * Роутинг: /rest/products
 */
@RestController
@RequestMapping("/rest/products")
public class ProductRestController {

    private final EntitiesService entitiesService;

    public ProductRestController(EntitiesService entitiesService) {
        this.entitiesService = entitiesService;
    }

    /**
     * Поиск продуктов по имени или идентификатору категории.
     * Если указан 'categoryId', возвращаются все продукты из данной категории и её подкатегорий (если предусмотрено).
     * Если указан 'name', производится поиск продуктов, чьё название содержит указанную строку.
     * При отсутствии обоих параметров возвращается HTTP 400.
     *
     * @param categoryId идентификатор категории (необязательный)
     * @param name       подстрока названия продукта для поиска (необязательная)
     * @return список найденных продуктов или ошибка 400, если параметры отсутствуют
     */
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

    /**
     * Получение списка наиболее популярных продуктов среди клиентов.
     * Популярность оценивается по количеству покупок. Количество возвращаемых продуктов ограничивается параметром 'limit'.
     *
     * @param limit максимальное число популярных продуктов в ответе
     * @return список популярных продуктов, отсортированных по количеству заказов по убыванию
     */
    @GetMapping("/popular")
    public ResponseEntity<List<Product>> getPopularProducts(@RequestParam Integer limit) {
        return ResponseEntity.ok(entitiesService.getTopPopularProducts(limit));
    }
}
