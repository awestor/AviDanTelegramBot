package ru.avilov.tgBot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;
import ru.avilov.tgBot.Entity.Client;
import ru.avilov.tgBot.Entity.ClientOrder;
import ru.avilov.tgBot.Entity.OrderProduct;
import ru.avilov.tgBot.Entity.Product;
import ru.avilov.tgBot.Repository.ClientOrderRepository;
import ru.avilov.tgBot.Repository.ClientRepository;
import ru.avilov.tgBot.Repository.OrderProductRepository;
import ru.avilov.tgBot.Repository.ProductRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ClientFillingTests {
    @Autowired private ClientRepository clientRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ClientOrderRepository clientOrderRepository;
    @Autowired private OrderProductRepository orderProductRepository;

    private final Map<String, Client> clientMap = new HashMap<>();
    private final Map<Integer, ClientOrder> orderMap = new HashMap<>();
    private final Map<String, Product> productMap = new HashMap<>();

    @Test
    public void fillClientsOrdersFromYaml() {
        Map<String, Object> yamlData = loadYamlAsProperties("data/client-data.yml");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> clients = (List<Map<String, Object>>) yamlData.get("clients");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> clientOrders = (List<Map<String, Object>>) yamlData.get("clientOrders");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> orderProducts = (List<Map<String, Object>>) yamlData.get("orderProducts");

        // Сохранение клиентов
        for (Map<String, Object> dto : clients) {
            Long externalId = Long.parseLong(dto.get("externalId").toString());
            String fullName = (String) dto.get("fullName");
            String phone = (String) dto.get("phoneNumber");
            String address = (String) dto.get("address");

            Client client = new Client();
            client.setExternalId(externalId);
            client.setFullName(fullName);
            client.setPhoneNumber(phone);
            client.setAddress(address);

            Client saved = clientRepository.save(client);
            clientMap.put(fullName, saved);
        }

        // Сохранение заказов
        int orderIndex = 1;
        for (Map<String, Object> dto : clientOrders) {
            String fullName = (String) dto.get("client");
            int status = (int) dto.get("status");
            double total = Double.parseDouble(dto.get("total").toString());

            Client client = clientMap.get(fullName);
            if (client == null) throw new IllegalStateException("Клиент не найден: " + fullName);

            ClientOrder order = new ClientOrder();
            order.setClient(client);
            order.setStatus(status);
            order.setTotal(total);

            ClientOrder saved = clientOrderRepository.save(order);
            orderMap.put(orderIndex++, saved);
        }

        // Кэш продуктов, загружаю для того чтобы знать их название и айди для связей в заказах
        productRepository.findAll().forEach(p -> productMap.put(p.getName(), p));

        // Сохранение позиций в заказах
        for (Map<String, Object> dto : orderProducts) {
            int orderId = (int) dto.get("clientOrder");
            String productName = (String) dto.get("product");
            int count = (int) dto.get("countProduct");

            ClientOrder order = orderMap.get(orderId);
            Product product = productMap.get(productName);

            if (order == null) throw new IllegalStateException("Заказ не найден: #" + orderId);
            if (product == null) throw new IllegalStateException("Товар не найден: " + productName);

            OrderProduct item = new OrderProduct();
            item.setClientOrder(order);
            item.setProduct(product);
            item.setCountProduct(count);
            orderProductRepository.save(item);
        }
    }

    private Map<String, Object> loadYamlAsProperties(String path) {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            return new Yaml().load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка загрузки YAML: " + path, e);
        }
    }
}

