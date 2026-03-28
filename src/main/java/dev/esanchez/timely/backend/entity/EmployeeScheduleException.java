package dev.esanchez.timely.backend.entity;

import dev.esanchez.timely.backend.util.ValidationUtils;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "employee_schedule_exceptions")
public class EmployeeScheduleException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_schedule_exception_id", nullable = false)
    private Long employeeScheduleExceptionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "reason")
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public EmployeeScheduleException() {
    }

    public EmployeeScheduleException(Employee employee,
                                     LocalDate date,
                                     String reason) {

        this.employee = ValidationUtils.requireNonNull(employee, "Employee cannot be null");
        this.date = ValidationUtils.requireNonNull(date, "Date cannot be null");
        this.reason = ValidationUtils.normalizeOptionalText(reason);
    }

    // Getters

    public Long getEmployeeScheduleExceptionId() {
        return employeeScheduleExceptionId;
    }

    public Employee getEmployee() {
        return employee;
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