package dev.esanchez.timely.backend.entity;

import dev.esanchez.timely.backend.util.ValidationUtils;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public Service() {
    }

    public Service(Business business,
                   String name,
                   String description) {

        this.business = ValidationUtils.requireNonNull(business, "Business cannot be null");
        this.name = ValidationUtils.validateText(name, "Name");
        this.description = ValidationUtils.normalizeOptionalText(description);
        this.isActive = true;
    }

    // Getters

    public Long getServiceId() {
        return serviceId;
    }

    public String getName() {
        return name;
    }

    public Business getBusiness() {
        return business;
    }

    public String getDescription() {
        return description;
    }

    public Boolean isActive() {
        return isActive;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    // Domain Methods

    public void updateName(String name) {
        this.name = ValidationUtils.validateText(name, "Name");
    }

    public void updateDescription(String description) {
        this.description = ValidationUtils.normalizeOptionalText(description);
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

}