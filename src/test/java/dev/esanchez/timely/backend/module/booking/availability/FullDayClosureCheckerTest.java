package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.business.exception.BusinessScheduleException;
import dev.esanchez.timely.backend.module.business.exception.BusinessScheduleExceptionRepository;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.employee.exception.EmployeeScheduleException;
import dev.esanchez.timely.backend.module.employee.exception.EmployeeScheduleExceptionRepository;
import dev.esanchez.timely.backend.module.services.Subservice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FullDayClosureCheckerTest {

    @Mock
    private BusinessScheduleExceptionRepository businessScheduleExceptionRepository;

    @Mock
    private EmployeeScheduleExceptionRepository employeeScheduleExceptionRepository;

    @InjectMocks
    private FullDayClosureChecker fullDayClosureChecker;

    @Test
    void shouldReturnTrueWhenBusinessIsClosed() {
        LocalDate date = LocalDate.of(2026, 4, 24);

        Business business = new Business();
        business.setBusinessId(10L);

        Employee employee = new Employee();
        employee.setEmployeeId(2L);

        AvailabilityContext context = new AvailabilityContext(
                employee,
                business,
                new Subservice(),
                date,
                ZoneId.of("Europe/Madrid"),
                30
        );

        when(businessScheduleExceptionRepository.findByBusiness_BusinessIdAndDate(10L, date))
                .thenReturn(Optional.of(new BusinessScheduleException()));
        when(employeeScheduleExceptionRepository.findByEmployee_EmployeeIdAndDate(2L, date))
                .thenReturn(Optional.empty());

        boolean result = fullDayClosureChecker.isClosed(context);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenEmployeeIsClosed() {
        LocalDate date = LocalDate.of(2026, 4, 24);

        Business business = new Business();
        business.setBusinessId(10L);

        Employee employee = new Employee();
        employee.setEmployeeId(2L);

        AvailabilityContext context = new AvailabilityContext(
                employee,
                business,
                new Subservice(),
                date,
                ZoneId.of("Europe/Madrid"),
                30
        );

        when(businessScheduleExceptionRepository.findByBusiness_BusinessIdAndDate(10L, date))
                .thenReturn(Optional.empty());
        when(employeeScheduleExceptionRepository.findByEmployee_EmployeeIdAndDate(2L, date))
                .thenReturn(Optional.of(new EmployeeScheduleException()));

        boolean result = fullDayClosureChecker.isClosed(context);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNobodyIsClosed() {
        LocalDate date = LocalDate.of(2026, 4, 24);

        Business business = new Business();
        business.setBusinessId(10L);

        Employee employee = new Employee();
        employee.setEmployeeId(2L);

        AvailabilityContext context = new AvailabilityContext(
                employee,
                business,
                new Subservice(),
                date,
                ZoneId.of("Europe/Madrid"),
                30
        );

        when(businessScheduleExceptionRepository.findByBusiness_BusinessIdAndDate(10L, date))
                .thenReturn(Optional.empty());
        when(employeeScheduleExceptionRepository.findByEmployee_EmployeeIdAndDate(2L, date))
                .thenReturn(Optional.empty());

        boolean result = fullDayClosureChecker.isClosed(context);

        assertThat(result).isFalse();
    }
}