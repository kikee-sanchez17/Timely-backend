package dev.esanchez.timely.backend.entity;

import dev.esanchez.timely.backend.util.ValidationUtils;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;


@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name ="business_id", nullable = false)
    private Business business;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "surname",nullable = false)
    private String surname;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public Employee() {}

    public Employee(Business business, String name, String surname) {

        this.business = ValidationUtils.requireNonNull(business, "Business cannot be null");
        this.name = ValidationUtils.validateText(name, "Name");
        this.surname = ValidationUtils.validateText(surname, "Name");
        this.isActive = true;

    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public Business getBusiness() {
        return business;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Boolean isActive() {
        return isActive;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    //Setters

    public void updateName(String name) {
        this.name = ValidationUtils.validateText(name, "Name");

    }

    public void updateSurname(String surname) {
        this.surname = ValidationUtils.validateText(surname, "Surname");
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
