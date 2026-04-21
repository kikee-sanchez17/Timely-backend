package dev.esanchez.timely.backend.module.employee.exception;

import dev.esanchez.timely.backend.module.shared.ExceptionIntervalType;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.utilsCommon.ValidationUtils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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

    public void updateTimeRange(LocalTime startTime, LocalTime endTime) {
        LocalTime validatedStart = ValidationUtils.requireNonNull(startTime, "Start time cannot be null");
        LocalTime validatedEnd = ValidationUtils.requireNonNull(endTime, "End time cannot be null");

        ValidationUtils.validateTimeRange(validatedStart, validatedEnd);

        this.startTime = validatedStart;
        this.endTime = validatedEnd;
    }

}