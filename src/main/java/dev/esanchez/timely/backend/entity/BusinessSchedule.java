package dev.esanchez.timely.backend.entity;

import dev.esanchez.timely.backend.util.ValidationUtils;
import jakarta.persistence.*;

import java.time.LocalTime;


@Entity
@Table(name = "business_schedule")
public class BusinessSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_schedule_id", nullable = false)
    private Long businessScheduleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(name = "day_of_week", nullable = false)
    private Short dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    public BusinessSchedule() {
    }

    public BusinessSchedule(Business business,
                            Short dayOfWeek,
                            LocalTime startTime,
                            LocalTime endTime) {

        this.business = ValidationUtils.requireNonNull(business, "Business cannot be null");
        this.dayOfWeek = validateDayOfWeek(dayOfWeek);
        this.startTime = ValidationUtils.requireNonNull(startTime, "Start time");
        this.endTime = ValidationUtils.requireNonNull(endTime, "End time");

        ValidationUtils.validateTimeRange(this.startTime, this.endTime);
    }

    // Getters

    public Long getBusinessScheduleId() {
        return businessScheduleId;
    }

    public Business getBusiness() {
        return business;
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

    // Setters

    public void updateTimeRange(LocalTime startTime, LocalTime endTime) {
        LocalTime validatedStart = ValidationUtils.requireNonNull(startTime, "Start time");
        LocalTime validatedEnd = ValidationUtils.requireNonNull(endTime, "End time");

        ValidationUtils.validateTimeRange(validatedStart, validatedEnd);

        this.startTime = validatedStart;
        this.endTime = validatedEnd;
    }

    public void updateDayOfWeek(Short dayOfWeek) {
        this.dayOfWeek = validateDayOfWeek(dayOfWeek);
    }

    // Validations

    private Short validateDayOfWeek(Short day) {
        if (day == null || day < 0 || day > 6) {
            throw new IllegalArgumentException("Day of week must be between 0 and 6");
        }
        return day;
    }

}