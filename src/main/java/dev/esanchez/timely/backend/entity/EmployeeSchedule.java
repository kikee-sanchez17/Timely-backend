package dev.esanchez.timely.backend.entity;

import dev.esanchez.timely.backend.util.ValidationUtils;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "employee_schedule")
public class EmployeeSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_schedule_id", nullable = false)
    private Long employeeScheduleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "day_of_week", nullable = false)
    private Short dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    public EmployeeSchedule() {
    }

    public EmployeeSchedule(Employee employee,
                            Short dayOfWeek,
                            LocalTime startTime,
                            LocalTime endTime) {

        this.employee = ValidationUtils.requireNonNull(employee, "Employee cannot be null");
        this.dayOfWeek = validateDayOfWeek(dayOfWeek);
        this.startTime = ValidationUtils.requireNonNull(startTime, "Start time");
        this.endTime = ValidationUtils.requireNonNull(endTime, "End time");

        ValidationUtils.validateTimeRange(this.startTime, this.endTime);
    }

    // Getters

    public Long getEmployeeScheduleId() {
        return employeeScheduleId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Short getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    // Domain Methods

    public void updateDayOfWeek(Short dayOfWeek) {
        this.dayOfWeek = validateDayOfWeek(dayOfWeek);
    }

    public void updateTimeRange(LocalTime startTime, LocalTime endTime) {
        LocalTime validatedStart = ValidationUtils.requireNonNull(startTime, "Start time");
        LocalTime validatedEnd = ValidationUtils.requireNonNull(endTime, "End time");

        ValidationUtils.validateTimeRange(validatedStart, validatedEnd);

        this.startTime = validatedStart;
        this.endTime = validatedEnd;
    }

    // Validations

    private Short validateDayOfWeek(Short day) {
        if (day == null || day < 0 || day > 6) {
            throw new IllegalArgumentException("Day of week must be between 0 and 6");
        }
        return day;
    }
}