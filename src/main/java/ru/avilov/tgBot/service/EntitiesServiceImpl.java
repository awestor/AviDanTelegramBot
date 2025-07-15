package ru.avilov.tgBot.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.avilov.tgBot.Entity.Client;
import ru.avilov.tgBot.Entity.ClientOrder;
import ru.avilov.tgBot.Entity.Product;
import ru.avilov.tgBot.Repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class EntitiesServiceImpl implements EntitiesService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ClientRepository clientRepository;

    public EntitiesServiceImpl(CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           ClientOrderRepository clientOrderRepository,
                           OrderProductRepository orderProductRepository,
                           ClientRepository clientRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.clientOrderRepository = clientOrderRepository;
        this.orderProductRepository = orderProductRepository;
        this.clientRepository = clientRepository;
    }
    @PersistenceContext private EntityManager entityManager;


    @Override
    @Transactional
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findProductsByCategoryId(categoryId);
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
        Pageable page = PageRequest.of(0, limit);
        return orderProductRepository.findPopularProductsOrdered(page);
    }

    @Override
    @Transactional
    public List<Client> searchClientsByName(String name) {
        String pattern = "%" + name + "%";
        return clientRepository.searchByNameContainsIgnoreCase(pattern);
    }

    @Override
    @Transactional
    public List<Product> searchProductsByName(String name) {
        String pattern = "%" + name + "%";
        return productRepository.findByNameLikeIgnoreCase(pattern);
    }
}
