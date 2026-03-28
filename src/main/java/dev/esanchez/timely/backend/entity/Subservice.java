package dev.esanchez.timely.backend.entity;

import dev.esanchez.timely.backend.util.ValidationUtils;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "subservices")
public class Subservice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subservice_id", nullable = false)
    private Long subserviceId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public Subservice() {
    }

    public Subservice(String name,
                      String description,
                      BigDecimal price,
                      Integer durationMinutes,
                      Service service) {

        this.name = ValidationUtils.validateText(name, "Name");
        this.description = ValidationUtils.normalizeOptionalText(description);
        this.price = validatePrice(price);
        this.durationMinutes = validateDurationMinutes(durationMinutes);
        this.service = ValidationUtils.requireNonNull(service, "Service cannot be null");
        this.isActive = true;
    }

    public Long getSubserviceId() {
        return subserviceId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public Service getService() {
        return service;
    }

    public Boolean isActive() {
        return isActive;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void updateName(String name) {
        this.name = ValidationUtils.validateText(name, "Name");
    }

    public void updateDescription(String description) {
        this.description = ValidationUtils.normalizeOptionalText(description);
    }

    public void updatePrice(BigDecimal price) {
        this.price = validatePrice(price);
    }

    public void updateDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = validateDurationMinutes(durationMinutes);
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    private BigDecimal validatePrice(BigDecimal price) {
        if (price == null) {
            return null;
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        return price;
    }

    private Integer validateDurationMinutes(Integer durationMinutes) {
        if (durationMinutes == null) {
            throw new IllegalArgumentException("Duration minutes cannot be null");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration minutes must be greater than 0");
        }
        return durationMinutes;
    }
}