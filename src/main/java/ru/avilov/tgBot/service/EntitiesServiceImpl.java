package ru.avilov.tgBot.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.avilov.tgBot.Entity.Client;
import ru.avilov.tgBot.Entity.ClientOrder;
import ru.avilov.tgBot.Entity.Product;
import ru.avilov.tgBot.Repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class EntitiesServiceImpl implements EntitiesService {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ClientOrderRepository clientOrderRepository;
    @Autowired private OrderProductRepository orderProductRepository;
    @Autowired private ClientRepository clientRepository;
    @PersistenceContext private EntityManager entityManager;


    @Override
    @Transactional
    public List<Product> getProductsByCategoryId(Long categoryId) {
        List<Long> allCategoryIds = new ArrayList<>();
        allCategoryIds.add(categoryId);
        allCategoryIds.addAll(categoryRepository.findChildIdsByParentId(categoryId));
        return productRepository.findProductsByCategoryIds(allCategoryIds);
    }

    @Override
    @Transactional
    public List<ClientOrder> getClientOrders(Long id) {
        return clientOrderRepository.findClientOrdersByClientId(id);
    }

    @Override
    @Transactional
    public List<Product> getClientProducts(Long id) {
        return orderProductRepository.findClientProductsByClientId(id);
    }

    @Override
    @Transactional
    public List<Product> getTopPopularProducts(Integer limit) {
        return entityManager.createQuery(
                "SELECT oi.product FROM OrderProduct oi GROUP BY oi.product ORDER BY SUM(oi.countProduct) DESC",
                Product.class
        ).setMaxResults(limit).getResultList();
    }

    @Override
    @Transactional
    public List<Client> searchClientsByName(String name) {
        String pattern = "%" + name.toLowerCase() + "%";
        return clientRepository.searchByNameContainsIgnoreCase(pattern);
    }

    @Override
    @Transactional
    public List<Product> searchProductsByName(String name) {
        String pattern = "%" + name.toLowerCase() + "%";
        return productRepository.findByNameLikeIgnoreCase(pattern);
    }
}
