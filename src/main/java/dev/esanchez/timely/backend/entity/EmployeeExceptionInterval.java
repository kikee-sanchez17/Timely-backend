package dev.esanchez.timely.backend.entity;

import dev.esanchez.timely.backend.util.ValidationUtils;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "employee_exception_intervals")
public class EmployeeExceptionInterval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_exception_interval_id", nullable = false)
    private Long employeeExceptionIntervalId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "interval_type", nullable = false)
    private ExceptionIntervalType intervalType;

    @Column(name = "reason")
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public EmployeeExceptionInterval() {
    }

    public EmployeeExceptionInterval(Employee employee,
                                     LocalDate date,
                                     LocalTime startTime,
                                     LocalTime endTime,
                                     ExceptionIntervalType intervalType,
                                     String reason) {

        this.employee = ValidationUtils.requireNonNull(employee, "Employee cannot be null");
        this.date = ValidationUtils.requireNonNull(date, "Date cannot be null");

        LocalTime validatedStart = ValidationUtils.requireNonNull(startTime, "Start time cannot be null");
        LocalTime validatedEnd = ValidationUtils.requireNonNull(endTime, "End time cannot be null");
        ValidationUtils.validateTimeRange(validatedStart, validatedEnd);

        this.startTime = validatedStart;
        this.endTime = validatedEnd;
        this.intervalType = ValidationUtils.requireNonNull(intervalType, "Interval type cannot be null");
        this.reason = ValidationUtils.normalizeOptionalText(reason);
    }

    public Long getEmployeeExceptionIntervalId() {
        return employeeExceptionIntervalId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public ExceptionIntervalType getIntervalType() {
        return intervalType;
    }

    public String getReason() {
        return reason;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateDate(LocalDate date) {
        this.date = ValidationUtils.requireNonNull(date, "Date cannot be null");
    }

    public void updateTimeRange(LocalTime startTime, LocalTime endTime) {
        LocalTime validatedStart = ValidationUtils.requireNonNull(startTime, "Start time cannot be null");
        LocalTime validatedEnd = ValidationUtils.requireNonNull(endTime, "End time cannot be null");

        ValidationUtils.validateTimeRange(validatedStart, validatedEnd);

        this.startTime = validatedStart;
        this.endTime = validatedEnd;
    }

    public void updateIntervalType(ExceptionIntervalType intervalType) {
        this.intervalType = ValidationUtils.requireNonNull(intervalType, "Interval type cannot be null");
    }

    public void updateReason(String reason) {
        this.reason = ValidationUtils.normalizeOptionalText(reason);
    }


}