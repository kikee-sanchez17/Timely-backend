package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.business.exception.BusinessScheduleExceptionRepository;
import dev.esanchez.timely.backend.module.employee.exception.EmployeeScheduleExceptionRepository;
import org.springframework.stereotype.Component;

@Component
public class FullDayClosureChecker {

    private final BusinessScheduleExceptionRepository businessScheduleExceptionRepository;
    private final EmployeeScheduleExceptionRepository employeeScheduleExceptionRepository;

    public FullDayClosureChecker(
            BusinessScheduleExceptionRepository businessScheduleExceptionRepository,
            EmployeeScheduleExceptionRepository employeeScheduleExceptionRepository
    ) {
        this.businessScheduleExceptionRepository = businessScheduleExceptionRepository;
        this.employeeScheduleExceptionRepository = employeeScheduleExceptionRepository;
    }

    public boolean isClosed(AvailabilityContext context) {
        boolean businessClosed = businessScheduleExceptionRepository
                .findByBusiness_BusinessIdAndDate(context.business().getBusinessId(), context.date())
                .isPresent();

        boolean employeeClosed = employeeScheduleExceptionRepository
                .findByEmployee_EmployeeIdAndDate(context.employee().getEmployeeId(), context.date())
                .isPresent();

        return businessClosed || employeeClosed;
    }
}