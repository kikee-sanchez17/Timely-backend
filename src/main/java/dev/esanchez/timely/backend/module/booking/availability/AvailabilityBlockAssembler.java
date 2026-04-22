package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.business.exception.BusinessExceptionInterval;
import dev.esanchez.timely.backend.module.business.exception.BusinessExceptionIntervalRepository;
import dev.esanchez.timely.backend.module.employee.exception.EmployeeExceptionInterval;
import dev.esanchez.timely.backend.module.employee.exception.EmployeeExceptionIntervalRepository;
import dev.esanchez.timely.backend.module.shared.ExceptionIntervalType;
import dev.esanchez.timely.backend.module.shared.TimeBlock;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvailabilityBlockAssembler {

    private final BusinessExceptionIntervalRepository businessExceptionIntervalRepository;
    private final EmployeeExceptionIntervalRepository employeeExceptionIntervalRepository;
    private final ExceptionIntervalBlockFactory exceptionIntervalBlockFactory;
    private final ScheduleBlockFactory scheduleBlockFactory;
    private final BookingBlockProvider bookingBlockProvider;

    public AvailabilityBlockAssembler(
            BusinessExceptionIntervalRepository businessExceptionIntervalRepository,
            EmployeeExceptionIntervalRepository employeeExceptionIntervalRepository,
            ExceptionIntervalBlockFactory exceptionIntervalBlockFactory,
            ScheduleBlockFactory scheduleBlockFactory,
            BookingBlockProvider bookingBlockProvider
    ) {
        this.businessExceptionIntervalRepository = businessExceptionIntervalRepository;
        this.employeeExceptionIntervalRepository = employeeExceptionIntervalRepository;
        this.exceptionIntervalBlockFactory = exceptionIntervalBlockFactory;
        this.scheduleBlockFactory = scheduleBlockFactory;
        this.bookingBlockProvider = bookingBlockProvider;
    }

    public AvailabilityBlocks assemble(AvailabilityContext context) {
        List<EmployeeExceptionInterval> employeeIntervals =
                employeeExceptionIntervalRepository.findByEmployee_EmployeeIdAndDate(
                        context.employee().getEmployeeId(),
                        context.date()
                );

        List<BusinessExceptionInterval> businessIntervals =
                businessExceptionIntervalRepository.findByBusiness_BusinessIdAndDate(
                        context.business().getBusinessId(),
                        context.date()
                );

        List<TimeBlock> businessOpenExceptionBlocks =
                exceptionIntervalBlockFactory.fromBusinessIntervals(
                        businessIntervals, ExceptionIntervalType.OPEN_INTERVAL, context.date(), context.zoneId());

        List<TimeBlock> businessClosedExceptionBlocks =
                exceptionIntervalBlockFactory.fromBusinessIntervals(
                        businessIntervals, ExceptionIntervalType.CLOSED_INTERVAL, context.date(), context.zoneId());

        List<TimeBlock> employeeOpenExceptionBlocks =
                exceptionIntervalBlockFactory.fromEmployeeIntervals(
                        employeeIntervals, ExceptionIntervalType.OPEN_INTERVAL, context.date(), context.zoneId());

        List<TimeBlock> employeeClosedExceptionBlocks =
                exceptionIntervalBlockFactory.fromEmployeeIntervals(
                        employeeIntervals, ExceptionIntervalType.CLOSED_INTERVAL, context.date(), context.zoneId());

        List<TimeBlock> businessScheduleBlocks =
                scheduleBlockFactory.businessBlocks(
                        context.business(), context.dayOfWeek(), context.zoneId(), context.date());

        List<TimeBlock> employeeScheduleBlocks =
                scheduleBlockFactory.employeeBlocks(
                        context.employee(), context.dayOfWeek(), context.zoneId(), context.date());

        List<TimeBlock> bookingBlocks = bookingBlockProvider.getBlockedBookingBlocks(context);

        List<TimeBlock> blockedTimeBlocks = TimeBlockOperations.buildBlockedTimeBlocks(
                bookingBlocks,
                businessClosedExceptionBlocks,
                employeeClosedExceptionBlocks
        );

        return new AvailabilityBlocks(
                employeeScheduleBlocks,
                employeeOpenExceptionBlocks,
                businessScheduleBlocks,
                businessOpenExceptionBlocks,
                blockedTimeBlocks
        );
    }
}
