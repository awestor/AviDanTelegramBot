package ru.avilov.tgBot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;
import ru.avilov.tgBot.Entity.Category;
import ru.avilov.tgBot.Entity.Product;
import ru.avilov.tgBot.Repository.CategoryRepository;
import ru.avilov.tgBot.Repository.ProductRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootTest
public class FillingTests {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    private final Map<String, Category> categoryMap = new HashMap<>();

    @Test
    public void fillFromYaml() {
        Map<String, Object> yamlData = loadYamlAsProperties("data/test-data.yml");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> categories = (List<Map<String, Object>>) yamlData.get("categories");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> products = (List<Map<String, Object>>) yamlData.get("products");

        // Сохранение категорий
        for (Map<String, Object> dto : categories) {
            String name = (String) dto.get("name");
            String parentName = dto.get("parent") != null ? (String) dto.get("parent") : null;
            Category parent = parentName != null ? categoryMap.get(parentName) : null;

            Category category = new Category();
            category.setName(name);
            category.setParent(parent);
            Category saved = categoryRepository.save(category);
            categoryMap.put(name, saved);
        }

        // Сохранение продуктов
        for (Map<String, Object> dto : products) {
            String categoryName = (String) dto.get("category");
            String name = (String) dto.get("name");
            String description = (String) dto.get("description");
            double price = Double.parseDouble(dto.get("price").toString());

            Category category = categoryMap.get(categoryName);
            if (category == null) throw new IllegalStateException("Категория не найдена: " + categoryName);

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            productRepository.save(product);
        }
    }

    private Map<String, Object> loadYamlAsProperties(String path) {
        Yaml yaml = new Yaml();
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            return yaml.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка загрузки YAML-файла: " + path, e);
        }
    }
}