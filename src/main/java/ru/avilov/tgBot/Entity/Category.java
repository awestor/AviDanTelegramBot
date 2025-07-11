package ru.avilov.tgBot.Entity;

import jakarta.persistence.*;

@Entity
public class Category {
    @Id
    @GeneratedValue
    private Long id;
    @Column(length = 50, unique = true, nullable = false)
    private String name;
    @ManyToOne()
    private Category parent;

    //Гетеры и Сетеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

}
