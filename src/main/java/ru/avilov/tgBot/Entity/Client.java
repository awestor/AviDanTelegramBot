package ru.avilov.tgBot.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Client {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true, nullable = false)
    private Long externalId;
    @Column(nullable = false)
    private String fullName;
    @Column(length = 15, nullable = false)
    private String phoneNumber;
    @Column(length = 400, nullable = false)
    private String address;

    //Гетеры и Сетеры
    public Long getId() {
        return id;
    }

    public Long getExternalId() {
        return externalId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setId(long id) {
        this.id = id;
    }

}
