package dev.esanchez.timely.backend.entity;

import dev.esanchez.timely.backend.util.ValidationUtils;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "business_schedule_exceptions")
public class BusinessScheduleException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_schedule_exception_id", nullable = false)
    private Long businessScheduleExceptionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "reason")
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public BusinessScheduleException() {
    }

    public BusinessScheduleException(Business business,
                                     LocalDate date,
                                     String reason) {

        this.business = ValidationUtils.requireNonNull(business, "Business cannot be null");
        this.date = ValidationUtils.requireNonNull(date, "Date cannot be null");
        this.reason = ValidationUtils.normalizeOptionalText(reason);
    }

    // Getters

    public Long getBusinessScheduleExceptionId() {
        return businessScheduleExceptionId;
    }

    public Business getBusiness() {
        return business;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getReason() {
        return reason;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    // Domain Methods

    public void updateReason(String reason) {
        this.reason = ValidationUtils.normalizeOptionalText(reason);
    }

}