package dev.esanchez.timely.backend.entity;

import dev.esanchez.timely.backend.util.ValidationUtils;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "start_datetime", nullable = false)
    private OffsetDateTime startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private OffsetDateTime endDatetime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subservice_id", nullable = false)
    private Subservice subservice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_user_id", nullable = false)
    private User customerUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "notes")
    private String notes;

    @Column(name = "cancel_reason")
    private String cancelReason;

    public Booking() {
    }

    public Booking(OffsetDateTime startDatetime,
                   OffsetDateTime endDatetime,
                   Subservice subservice,
                   User customerUser,
                   Employee employee,
                   String notes) {

        OffsetDateTime validatedStart =
                ValidationUtils.requireNonNull(startDatetime, "Start datetime cannot be null");
        OffsetDateTime validatedEnd =
                ValidationUtils.requireNonNull(endDatetime, "End datetime cannot be null");

        validateDateTimeRange(validatedStart, validatedEnd);

        this.startDatetime = validatedStart;
        this.endDatetime = validatedEnd;
        this.subservice = ValidationUtils.requireNonNull(subservice, "Subservice cannot be null");
        this.customerUser = ValidationUtils.requireNonNull(customerUser, "Customer user cannot be null");
        this.employee = ValidationUtils.requireNonNull(employee, "Employee cannot be null");
        this.status = BookingStatus.PENDING;
        this.notes = ValidationUtils.normalizeOptionalText(notes);
        this.cancelReason = null;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public OffsetDateTime getStartDatetime() {
        return startDatetime;
    }

    public OffsetDateTime getEndDatetime() {
        return endDatetime;
    }

    public Subservice getSubservice() {
        return subservice;
    }

    public User getCustomerUser() {
        return customerUser;
    }

    public Employee getEmployee() {
        return employee;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getNotes() {
        return notes;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void updateNotes(String notes) {
        this.notes = ValidationUtils.normalizeOptionalText(notes);
    }

    public void reschedule(OffsetDateTime startDatetime, OffsetDateTime endDatetime) {
        OffsetDateTime validatedStart =
                ValidationUtils.requireNonNull(startDatetime, "Start datetime cannot be null");
        OffsetDateTime validatedEnd =
                ValidationUtils.requireNonNull(endDatetime, "End datetime cannot be null");

        validateDateTimeRange(validatedStart, validatedEnd);

        this.startDatetime = validatedStart;
        this.endDatetime = validatedEnd;
    }

    public void confirm() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }
        this.status = BookingStatus.CONFIRMED;
    }

    public void complete() {
        if (this.status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be completed");
        }
        this.status = BookingStatus.COMPLETED;
    }

    public void markNoShow() {
        if (this.status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be marked as no show");
        }
        this.status = BookingStatus.NO_SHOW;
    }

    public void cancelByCustomer(String cancelReason) {
        if (this.status == BookingStatus.COMPLETED || this.status == BookingStatus.NO_SHOW) {
            throw new IllegalStateException("Completed or no-show bookings cannot be cancelled");
        }
        this.status = BookingStatus.CANCELLED_BY_CUSTOMER;
        this.cancelReason = ValidationUtils.normalizeOptionalText(cancelReason);
    }

    public void cancelByBusiness(String cancelReason) {
        if (this.status == BookingStatus.COMPLETED || this.status == BookingStatus.NO_SHOW) {
            throw new IllegalStateException("Completed or no-show bookings cannot be cancelled");
        }
        this.status = BookingStatus.CANCELLED_BY_BUSINESS;
        this.cancelReason = ValidationUtils.normalizeOptionalText(cancelReason);
    }

    private void validateDateTimeRange(OffsetDateTime startDatetime, OffsetDateTime endDatetime) {
        if (!startDatetime.isBefore(endDatetime)) {
            throw new IllegalArgumentException("Start datetime must be before end datetime");
        }
    }
}