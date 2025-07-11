package ru.avilov.tgBot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import ru.avilov.tgBot.Entity.Category;
import ru.avilov.tgBot.Entity.Product;
import ru.avilov.tgBot.Repository.CategoryRepository;
import ru.avilov.tgBot.Repository.ProductRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@SpringBootTest
public class FillingTests {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;

    private final Map<String, Category> categoryMap = new HashMap<>();

    @Test
    public void fillFromYaml() {
        Properties props = loadYamlAsProperties("data/test-data.yml");
        if (props == null) throw new IllegalStateException("YAML-файл не загружен.");

        // Сохраняю категории
        int catIndex = 0;
        while (true) {
            String prefix = "test-data.categories[" + catIndex + "]";
            String name = props.getProperty(prefix + ".name");
            if (name == null) break;

            String parentName = props.getProperty(prefix + ".parent");
            Category parent = parentName != null ? categoryMap.get(parentName) : null;

            Category category = new Category();
            category.setName(name);
            category.setParent(parent);
            Category saved = categoryRepository.save(category);
            categoryMap.put(name, saved);

            catIndex++;
        }

        // Сохраняю продукты
        int prodIndex = 0;
        while (true) {
            String prefix = "test-data.products[" + prodIndex + "]";
            String categoryName = props.getProperty(prefix + ".category");
            if (categoryName == null) break;

            String name = props.getProperty(prefix + ".name");
            String description = props.getProperty(prefix + ".description");
            double price = Double.parseDouble(props.getProperty(prefix + ".price"));

            Category category = categoryMap.get(categoryName);
            if (category == null) throw new IllegalStateException("Категория не найдена: " + categoryName);

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            productRepository.save(product);

            prodIndex++;
        }
    }

    private Properties loadYamlAsProperties(String path) {
        YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
        yamlFactory.setResources(new ClassPathResource(path));
        return yamlFactory.getObject();
    }
}