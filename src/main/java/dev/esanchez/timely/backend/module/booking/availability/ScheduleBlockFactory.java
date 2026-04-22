package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.business.BusinessSchedule;
import dev.esanchez.timely.backend.module.business.BusinessScheduleRepository;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.employee.EmployeeSchedule;
import dev.esanchez.timely.backend.module.employee.EmployeeScheduleRepository;
import dev.esanchez.timely.backend.module.shared.TimeBlock;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduleBlockFactory {

    private final BusinessScheduleRepository businessScheduleRepository;
    private final EmployeeScheduleRepository employeeScheduleRepository;

    public ScheduleBlockFactory(
            BusinessScheduleRepository businessScheduleRepository,
            EmployeeScheduleRepository employeeScheduleRepository
    ) {
        this.businessScheduleRepository = businessScheduleRepository;
        this.employeeScheduleRepository = employeeScheduleRepository;
    }

    public List<TimeBlock> businessBlocks(Business business, int dayOfWeek, ZoneId zoneId, LocalDate date) {
        List<BusinessSchedule> schedules =
                businessScheduleRepository.findAllByBusiness_businessIdAndDayOfWeek(business.getBusinessId(), dayOfWeek);

        List<TimeBlock> result = new ArrayList<>();
        for (BusinessSchedule schedule : schedules) {
            result.add(TimeBlockMapper.toTimeBlock(date, schedule.getStartTime(), schedule.getEndTime(), zoneId));
        }
        return result;
    }

    public List<TimeBlock> employeeBlocks(Employee employee, int dayOfWeek, ZoneId zoneId, LocalDate date) {
        List<EmployeeSchedule> schedules =
                employeeScheduleRepository.findAllByEmployee_employeeIdAndDayOfWeek(employee.getEmployeeId(), dayOfWeek);

        List<TimeBlock> result = new ArrayList<>();
        for (EmployeeSchedule schedule : schedules) {
            result.add(TimeBlockMapper.toTimeBlock(date, schedule.getStartTime(), schedule.getEndTime(), zoneId));
        }
        return result;
    }
}
