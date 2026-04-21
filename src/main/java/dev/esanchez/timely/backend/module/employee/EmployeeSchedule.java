package dev.esanchez.timely.backend.module.employee;

import dev.esanchez.timely.backend.module.utilsCommon.ValidationUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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


    public void updateTimeRange(LocalTime startTime, LocalTime endTime) {
        LocalTime validatedStart = ValidationUtils.requireNonNull(startTime, "Start time");
        LocalTime validatedEnd = ValidationUtils.requireNonNull(endTime, "End time");

        ValidationUtils.validateTimeRange(validatedStart, validatedEnd);

        this.startTime = validatedStart;
        this.endTime = validatedEnd;
    }

}