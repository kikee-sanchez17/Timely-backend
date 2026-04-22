package dev.esanchez.timely.backend.module.booking;

import dev.esanchez.timely.backend.module.booking.availability.*;
import dev.esanchez.timely.backend.module.booking.creator.BookingCreator;
import dev.esanchez.timely.backend.module.booking.dto.request.AvailableSlotRequest;
import dev.esanchez.timely.backend.module.booking.dto.request.CreateBookingRequest;
import dev.esanchez.timely.backend.module.booking.dto.response.AvailableSlotDTO;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.services.Subservice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingCreator bookingCreator;

    @Mock
    private BookingLoader bookingLoader;

    @Mock
    private AvailabilityContextFactory availabilityContextFactory;

    @Mock
    private FullDayClosureChecker fullDayClosureChecker;

    @Mock
    private AvailabilityBlockAssembler availabilityBlockAssembler;

    @Mock
    private AvailableSlotCalculator availableSlotCalculator;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void shouldDelegateCreateBookingToBookingCreator() {
        CreateBookingRequest request = new CreateBookingRequest();
        Booking expectedBooking = new Booking();

        when(bookingCreator.create(request)).thenReturn(expectedBooking);

        Booking result = bookingService.createBooking(request);

        assertThat(result).isSameAs(expectedBooking);
        verify(bookingCreator).create(request);
    }

    @Test
    void shouldReturnEmptyListWhenClosedForFullDay() {
        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setDate(LocalDate.of(2026, 4, 24));

        Employee employee = new Employee();
        Business business = new Business();
        Subservice subservice = new Subservice();

        AvailabilityData data = new AvailabilityData(employee, business, subservice);
        AvailabilityContext context = new AvailabilityContext(
                employee,
                business,
                subservice,
                request.getDate(),
                ZoneId.of("Europe/Madrid"),
                30
        );

        when(bookingLoader.loadAvailabilityData(request)).thenReturn(data);
        when(availabilityContextFactory.create(data, request)).thenReturn(context);
        when(fullDayClosureChecker.isClosed(context)).thenReturn(true);

        List<AvailableSlotDTO> result = bookingService.calculateAvailableSlots(request);

        assertThat(result).isEmpty();
        verify(availabilityBlockAssembler, never()).assemble(any());
        verify(availableSlotCalculator, never()).calculate(any(), any());
    }

    @Test
    void shouldAssembleBlocksAndCalculateSlotsWhenOpen() {
        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setDate(LocalDate.of(2026, 4, 24));

        Employee employee = new Employee();
        Business business = new Business();
        Subservice subservice = new Subservice();

        AvailabilityData data = new AvailabilityData(employee, business, subservice);
        AvailabilityContext context = new AvailabilityContext(
                employee,
                business,
                subservice,
                request.getDate(),
                ZoneId.of("Europe/Madrid"),
                30
        );
        AvailabilityBlocks blocks = new AvailabilityBlocks(
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );

        List<AvailableSlotDTO> expectedSlots = List.of(new AvailableSlotDTO(null, null, 30));

        when(bookingLoader.loadAvailabilityData(request)).thenReturn(data);
        when(availabilityContextFactory.create(data, request)).thenReturn(context);
        when(fullDayClosureChecker.isClosed(context)).thenReturn(false);
        when(availabilityBlockAssembler.assemble(context)).thenReturn(blocks);
        when(availableSlotCalculator.calculate(blocks, context)).thenReturn(expectedSlots);

        List<AvailableSlotDTO> result = bookingService.calculateAvailableSlots(request);

        assertThat(result).isEqualTo(expectedSlots);
        verify(availabilityBlockAssembler).assemble(context);
        verify(availableSlotCalculator).calculate(blocks, context);
    }
}
