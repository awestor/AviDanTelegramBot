package ru.avilov.tgBot.Entity;

import jakarta.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Category category;
    @Column(length = 50, unique = true, nullable = false)
    private String name;
    @Column(length = 400, nullable = false)
    private String description;
    @Column(nullable = false)
    private Double price;

    //Гетеры и Сетеры
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
