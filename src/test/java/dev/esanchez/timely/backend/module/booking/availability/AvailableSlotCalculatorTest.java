package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.booking.dto.response.AvailableSlotDTO;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.services.Subservice;
import dev.esanchez.timely.backend.module.shared.TimeBlock;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AvailableSlotCalculatorTest {

    private final AvailableSlotCalculator availableSlotCalculator = new AvailableSlotCalculator();

    private AvailabilityContext buildContext(LocalDate date, int durationMinutes) {
        Employee employee = new Employee();
        Business business = new Business();
        Subservice subservice = new Subservice();
        subservice.setDurationMinutes(durationMinutes);

        return new AvailabilityContext(
                employee,
                business,
                subservice,
                date,
                ZoneId.of("Europe/Madrid"),
                durationMinutes
        );
    }

    @Test
    void shouldReturnAvailableSlotsWhenThereAreNoBookings() {
        LocalDate date = LocalDate.of(2026, 4, 24);
        AvailabilityContext context = buildContext(date, 30);

        TimeBlock businessSchedule1 = new TimeBlock(
                OffsetDateTime.parse("2026-04-24T10:00:00+02:00"),
                OffsetDateTime.parse("2026-04-24T14:00:00+02:00")
        );

        TimeBlock businessSchedule2 = new TimeBlock(
                OffsetDateTime.parse("2026-04-24T16:00:00+02:00"),
                OffsetDateTime.parse("2026-04-24T20:00:00+02:00")
        );

        AvailabilityBlocks availabilityBlocks = new AvailabilityBlocks(
                List.of(),
                List.of(),
                List.of(businessSchedule1, businessSchedule2),
                List.of(),
                List.of()
        );

        List<AvailableSlotDTO> result = availableSlotCalculator.calculate(availabilityBlocks, context);

        assertThat(result).hasSize(16);
    }

    @Test
    void shouldReturnAvailableSlotsWhenThereIsOneBooking() {
        LocalDate date = LocalDate.of(2026, 4, 1);
        AvailabilityContext context = buildContext(date, 30);

        TimeBlock businessSchedule = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T10:00:00+02:00"),
                OffsetDateTime.parse("2026-04-01T14:00:00+02:00")
        );

        TimeBlock bookingBlock = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T11:00:00+02:00"),
                OffsetDateTime.parse("2026-04-01T11:30:00+02:00")
        );

        AvailabilityBlocks availabilityBlocks = new AvailabilityBlocks(
                List.of(),
                List.of(),
                List.of(businessSchedule),
                List.of(),
                List.of(bookingBlock)
        );

        List<AvailableSlotDTO> result = availableSlotCalculator.calculate(availabilityBlocks, context);

        assertThat(result).hasSize(7);
    }

    @Test
    void shouldReturnAvailableSlotsWhenThereIsOneBookingAtTheStartBusinessSchedule() {
        LocalDate date = LocalDate.of(2026, 4, 1);
        AvailabilityContext context = buildContext(date, 30);

        TimeBlock businessSchedule = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T10:00:00+02:00"),
                OffsetDateTime.parse("2026-04-01T14:00:00+02:00")
        );

        TimeBlock bookingBlock = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T10:00:00+02:00"),
                OffsetDateTime.parse("2026-04-01T10:30:00+02:00")
        );

        AvailabilityBlocks availabilityBlocks = new AvailabilityBlocks(
                List.of(),
                List.of(),
                List.of(businessSchedule),
                List.of(),
                List.of(bookingBlock)
        );

        List<AvailableSlotDTO> result = availableSlotCalculator.calculate(availabilityBlocks, context);

        assertThat(result).hasSize(7);
    }

    @Test
    void shouldReturnAvailableSlotsWhenThereIsOneBookingAtTheEndBusinessSchedule() {
        LocalDate date = LocalDate.of(2026, 4, 1);
        AvailabilityContext context = buildContext(date, 30);

        TimeBlock businessSchedule = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T10:00:00+02:00"),
                OffsetDateTime.parse("2026-04-01T14:00:00+02:00")
        );

        TimeBlock bookingBlock = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T13:30:00+02:00"),
                OffsetDateTime.parse("2026-04-01T14:00:00+02:00")
        );

        AvailabilityBlocks availabilityBlocks = new AvailabilityBlocks(
                List.of(),
                List.of(),
                List.of(businessSchedule),
                List.of(),
                List.of(bookingBlock)
        );

        List<AvailableSlotDTO> result = availableSlotCalculator.calculate(availabilityBlocks, context);

        assertThat(result).hasSize(7);

        assertThat(result.get(0).getStartDatetime())
                .isEqualTo(OffsetDateTime.parse("2026-04-01T10:00:00+02:00"));
        assertThat(result.get(0).getEndDatetime())
                .isEqualTo(OffsetDateTime.parse("2026-04-01T10:30:00+02:00"));

        assertThat(result.get(6).getStartDatetime())
                .isEqualTo(OffsetDateTime.parse("2026-04-01T13:00:00+02:00"));
        assertThat(result.get(6).getEndDatetime())
                .isEqualTo(OffsetDateTime.parse("2026-04-01T13:30:00+02:00"));

        assertThat(result)
                .noneMatch(slot ->
                        slot.getStartDatetime().equals(OffsetDateTime.parse("2026-04-01T13:30:00+02:00")) &&
                                slot.getEndDatetime().equals(OffsetDateTime.parse("2026-04-01T14:00:00+02:00"))
                );
    }

    @Test
    void shouldReturnAvailableSlotsWhenThereAreTwoBookings() {
        LocalDate date = LocalDate.of(2026, 4, 1);
        AvailabilityContext context = buildContext(date, 30);

        TimeBlock businessSchedule = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T10:00:00+02:00"),
                OffsetDateTime.parse("2026-04-01T14:00:00+02:00")
        );

        TimeBlock bookingBlock1 = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T11:00:00+02:00"),
                OffsetDateTime.parse("2026-04-01T11:30:00+02:00")
        );

        TimeBlock bookingBlock2 = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T12:00:00+02:00"),
                OffsetDateTime.parse("2026-04-01T13:00:00+02:00")
        );

        AvailabilityBlocks availabilityBlocks = new AvailabilityBlocks(
                List.of(),
                List.of(),
                List.of(businessSchedule),
                List.of(),
                List.of(bookingBlock1, bookingBlock2)
        );

        List<AvailableSlotDTO> result = availableSlotCalculator.calculate(availabilityBlocks, context);

        assertThat(result).hasSize(5);
    }

    @Test
    void shouldReturnNoSlotsWhenGapIsSmallerThanSubserviceDuration() {
        LocalDate date = LocalDate.of(2026, 4, 1);
        AvailabilityContext context = buildContext(date, 30);

        TimeBlock businessSchedule = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T10:00:00+02:00"),
                OffsetDateTime.parse("2026-04-01T11:00:00+02:00")
        );

        TimeBlock bookingBlock = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T10:20:00+02:00"),
                OffsetDateTime.parse("2026-04-01T11:00:00+02:00")
        );

        AvailabilityBlocks availabilityBlocks = new AvailabilityBlocks(
                List.of(),
                List.of(),
                List.of(businessSchedule),
                List.of(),
                List.of(bookingBlock)
        );

        List<AvailableSlotDTO> result = availableSlotCalculator.calculate(availabilityBlocks, context);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnOneSlotWhenGapIsExactlyEqualToSubserviceDuration() {
        LocalDate date = LocalDate.of(2026, 4, 1);
        AvailabilityContext context = buildContext(date, 30);

        TimeBlock businessSchedule = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T10:00:00+02:00"),
                OffsetDateTime.parse("2026-04-01T11:00:00+02:00")
        );

        TimeBlock bookingBlock = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T10:30:00+02:00"),
                OffsetDateTime.parse("2026-04-01T11:00:00+02:00")
        );

        AvailabilityBlocks availabilityBlocks = new AvailabilityBlocks(
                List.of(),
                List.of(),
                List.of(businessSchedule),
                List.of(),
                List.of(bookingBlock)
        );

        List<AvailableSlotDTO> result = availableSlotCalculator.calculate(availabilityBlocks, context);

        assertThat(result).hasSize(1);

        assertThat(result.get(0).getStartDatetime())
                .isEqualTo(OffsetDateTime.parse("2026-04-01T10:00:00+02:00"));
        assertThat(result.get(0).getEndDatetime())
                .isEqualTo(OffsetDateTime.parse("2026-04-01T10:30:00+02:00"));
    }

    @Test
    void shouldReturnAvailableSlotsWhenThereIsOneBookingInTwoBusinessSchedules() {
        LocalDate date = LocalDate.of(2026, 4, 1);
        AvailabilityContext context = buildContext(date, 30);

        TimeBlock businessSchedule1 = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T10:00:00+02:00"),
                OffsetDateTime.parse("2026-04-01T14:00:00+02:00")
        );

        TimeBlock businessSchedule2 = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T16:00:00+02:00"),
                OffsetDateTime.parse("2026-04-01T20:00:00+02:00")
        );

        TimeBlock bookingBlock1 = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T12:30:00+02:00"),
                OffsetDateTime.parse("2026-04-01T13:00:00+02:00")
        );

        TimeBlock bookingBlock2 = new TimeBlock(
                OffsetDateTime.parse("2026-04-01T18:00:00+02:00"),
                OffsetDateTime.parse("2026-04-01T19:00:00+02:00")
        );

        AvailabilityBlocks availabilityBlocks = new AvailabilityBlocks(
                List.of(),
                List.of(),
                List.of(businessSchedule1, businessSchedule2),
                List.of(),
                List.of(bookingBlock1, bookingBlock2)
        );

        List<AvailableSlotDTO> result = availableSlotCalculator.calculate(availabilityBlocks, context);

        assertThat(result).hasSize(13);

        assertThat(result.getFirst().getStartDatetime())
                .isEqualTo(OffsetDateTime.parse("2026-04-01T10:00:00+02:00"));
        assertThat(result.getFirst().getEndDatetime())
                .isEqualTo(OffsetDateTime.parse("2026-04-01T10:30:00+02:00"));

        assertThat(result.get(12).getStartDatetime())
                .isEqualTo(OffsetDateTime.parse("2026-04-01T19:30:00+02:00"));
        assertThat(result.get(12).getEndDatetime())
                .isEqualTo(OffsetDateTime.parse("2026-04-01T20:00:00+02:00"));
    }

    @Test
    void shouldReturnAvailableSlotsWhenThereAreNoBookingsAndThereIsEmployeeSchedule() {
        LocalDate date = LocalDate.of(2026, 4, 24);
        AvailabilityContext context = buildContext(date, 30);

        TimeBlock employeeSchedule = new TimeBlock(
                OffsetDateTime.parse("2026-04-24T12:00:00+02:00"),
                OffsetDateTime.parse("2026-04-24T14:00:00+02:00")
        );

        AvailabilityBlocks availabilityBlocks = new AvailabilityBlocks(
                List.of(employeeSchedule),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );

        List<AvailableSlotDTO> result = availableSlotCalculator.calculate(availabilityBlocks, context);

        assertThat(result).hasSize(4);
    }

    @Test
    void shouldReturnCorrectSlotsWhenThereIsABusinessClosedBlock() {
        LocalDate date = LocalDate.of(2026, 4, 24);
        AvailabilityContext context = buildContext(date, 30);

        TimeBlock employeeSchedule = new TimeBlock(
                OffsetDateTime.parse("2026-04-24T09:00:00+02:00"),
                OffsetDateTime.parse("2026-04-24T11:00:00+02:00")
        );

        TimeBlock businessClosedBlock = new TimeBlock(
                OffsetDateTime.parse("2026-04-24T09:30:00+02:00"),
                OffsetDateTime.parse("2026-04-24T10:00:00+02:00")
        );

        AvailabilityBlocks availabilityBlocks = new AvailabilityBlocks(
                List.of(employeeSchedule),
                List.of(),
                List.of(),
                List.of(),
                List.of(businessClosedBlock)
        );

        List<AvailableSlotDTO> result = availableSlotCalculator.calculate(availabilityBlocks, context);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getStartDatetime().toLocalTime()).isEqualTo(java.time.LocalTime.of(9, 0));
        assertThat(result.get(1).getStartDatetime().toLocalTime()).isEqualTo(java.time.LocalTime.of(10, 0));
        assertThat(result.get(2).getStartDatetime().toLocalTime()).isEqualTo(java.time.LocalTime.of(10, 30));
    }

    @Test
    void shouldNotDuplicateSlotsWhenBusinessOpenIntervalOverlapsBusinessSchedule() {
        LocalDate date = LocalDate.of(2026, 4, 13);
        AvailabilityContext context = buildContext(date, 30);

        TimeBlock businessSchedule = new TimeBlock(
                OffsetDateTime.parse("2026-04-13T10:00:00+02:00"),
                OffsetDateTime.parse("2026-04-13T14:00:00+02:00")
        );

        TimeBlock businessOpenInterval = new TimeBlock(
                OffsetDateTime.parse("2026-04-13T12:00:00+02:00"),
                OffsetDateTime.parse("2026-04-13T13:00:00+02:00")
        );

        AvailabilityBlocks availabilityBlocks = new AvailabilityBlocks(
                List.of(),
                List.of(),
                List.of(businessSchedule),
                List.of(businessOpenInterval),
                List.of()
        );

        List<AvailableSlotDTO> result = availableSlotCalculator.calculate(availabilityBlocks, context);

        assertThat(result).hasSize(8);

        assertThat(result)
                .extracting(AvailableSlotDTO::getStartDatetime)
                .containsExactly(
                        OffsetDateTime.parse("2026-04-13T10:00:00+02:00"),
                        OffsetDateTime.parse("2026-04-13T10:30:00+02:00"),
                        OffsetDateTime.parse("2026-04-13T11:00:00+02:00"),
                        OffsetDateTime.parse("2026-04-13T11:30:00+02:00"),
                        OffsetDateTime.parse("2026-04-13T12:00:00+02:00"),
                        OffsetDateTime.parse("2026-04-13T12:30:00+02:00"),
                        OffsetDateTime.parse("2026-04-13T13:00:00+02:00"),
                        OffsetDateTime.parse("2026-04-13T13:30:00+02:00")
                );

        assertThat(result)
                .extracting(AvailableSlotDTO::getEndDatetime)
                .containsExactly(
                        OffsetDateTime.parse("2026-04-13T10:30:00+02:00"),
                        OffsetDateTime.parse("2026-04-13T11:00:00+02:00"),
                        OffsetDateTime.parse("2026-04-13T11:30:00+02:00"),
                        OffsetDateTime.parse("2026-04-13T12:00:00+02:00"),
                        OffsetDateTime.parse("2026-04-13T12:30:00+02:00"),
                        OffsetDateTime.parse("2026-04-13T13:00:00+02:00"),
                        OffsetDateTime.parse("2026-04-13T13:30:00+02:00"),
                        OffsetDateTime.parse("2026-04-13T14:00:00+02:00")
                );
    }
}