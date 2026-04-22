package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.business.exception.BusinessExceptionInterval;
import dev.esanchez.timely.backend.module.business.exception.BusinessExceptionIntervalRepository;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.employee.exception.EmployeeExceptionIntervalRepository;
import dev.esanchez.timely.backend.module.services.Subservice;
import dev.esanchez.timely.backend.module.shared.ExceptionIntervalType;
import dev.esanchez.timely.backend.module.shared.TimeBlock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityBlockAssemblerTest {

    @Mock
    private BusinessExceptionIntervalRepository businessExceptionIntervalRepository;

    @Mock
    private EmployeeExceptionIntervalRepository employeeExceptionIntervalRepository;

    @Mock
    private ExceptionIntervalBlockFactory exceptionIntervalBlockFactory;

    @Mock
    private ScheduleBlockFactory scheduleBlockFactory;

    @Mock
    private BookingBlockProvider bookingBlockProvider;

    @InjectMocks
    private AvailabilityBlockAssembler availabilityBlockAssembler;

    @Test
    void shouldAssembleBlockedTimeBlocksFromBookingsAndClosedIntervals() {
        LocalDate date = LocalDate.of(2026, 4, 24);
        ZoneId zoneId = ZoneId.of("Europe/Madrid");

        Business business = new Business();
        business.setBusinessId(10L);

        Employee employee = new Employee();
        employee.setEmployeeId(2L);

        Subservice subservice = new Subservice();
        subservice.setDurationMinutes(30);

        AvailabilityContext context = new AvailabilityContext(
                employee,
                business,
                subservice,
                date,
                zoneId,
                30
        );

        BusinessExceptionInterval closedInterval = new BusinessExceptionInterval();
        closedInterval.setDate(date);
        closedInterval.updateTimeRange(LocalTime.of(9, 30), LocalTime.of(10, 0));
        closedInterval.setIntervalType(ExceptionIntervalType.CLOSED_INTERVAL);

        TimeBlock businessClosedBlock = new TimeBlock(
                OffsetDateTime.parse("2026-04-24T09:30:00+02:00"),
                OffsetDateTime.parse("2026-04-24T10:00:00+02:00")
        );

        TimeBlock bookingBlock = new TimeBlock(
                OffsetDateTime.parse("2026-04-24T10:30:00+02:00"),
                OffsetDateTime.parse("2026-04-24T11:00:00+02:00")
        );

        when(employeeExceptionIntervalRepository.findByEmployee_EmployeeIdAndDate(2L, date))
                .thenReturn(List.of());
        when(businessExceptionIntervalRepository.findByBusiness_BusinessIdAndDate(10L, date))
                .thenReturn(List.of(closedInterval));

        when(exceptionIntervalBlockFactory.fromBusinessIntervals(
                List.of(closedInterval), ExceptionIntervalType.OPEN_INTERVAL, date, zoneId))
                .thenReturn(List.of());

        when(exceptionIntervalBlockFactory.fromBusinessIntervals(
                List.of(closedInterval), ExceptionIntervalType.CLOSED_INTERVAL, date, zoneId))
                .thenReturn(List.of(businessClosedBlock));

        when(exceptionIntervalBlockFactory.fromEmployeeIntervals(
                List.of(), ExceptionIntervalType.OPEN_INTERVAL, date, zoneId))
                .thenReturn(List.of());

        when(exceptionIntervalBlockFactory.fromEmployeeIntervals(
                List.of(), ExceptionIntervalType.CLOSED_INTERVAL, date, zoneId))
                .thenReturn(List.of());

        when(scheduleBlockFactory.businessBlocks(business, context.dayOfWeek(), zoneId, date))
                .thenReturn(List.of());

        when(scheduleBlockFactory.employeeBlocks(employee, context.dayOfWeek(), zoneId, date))
                .thenReturn(List.of());

        when(bookingBlockProvider.getBlockedBookingBlocks(context))
                .thenReturn(List.of(bookingBlock));

        AvailabilityBlocks result = availabilityBlockAssembler.assemble(context);

        assertThat(result.blockedTimeBlocks()).hasSize(2);
        assertThat(result.blockedTimeBlocks()).containsExactly(businessClosedBlock, bookingBlock);
    }
}