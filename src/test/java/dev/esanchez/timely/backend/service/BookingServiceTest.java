package dev.esanchez.timely.backend.service;

import dev.esanchez.timely.backend.dto.request.AvailableSlotRequest;
import dev.esanchez.timely.backend.dto.response.AvailableSlotDTO;
import dev.esanchez.timely.backend.entity.*;
import dev.esanchez.timely.backend.repository.*;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SubserviceRepository subserviceRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BusinessScheduleRepository businessScheduleRepository;

    @Mock
    private EmployeeScheduleRepository employeeScheduleRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void shouldReturnAvailableSlotsWhenThereAreNoBookings() {
        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setCustomerUserId(1L);
        request.setEmployeeId(2L);
        request.setSubserviceId(3L);
        request.setDate(LocalDate.of(2026, 4, 24));

        Employee employee = new Employee();
        employee.setEmployeeId(2L);

        Business business = new Business();
        business.setBusinessId(10L);

        CountryTimezoneId countryTimezoneId = new CountryTimezoneId("ES","Europe/Madrid");

        CountryTimezone countryTimezone = new CountryTimezone();
        countryTimezone.setId(countryTimezoneId);

        business.setCountryTimezone(countryTimezone);
        employee.setBusiness(business);

        Subservice subservice = new Subservice();
        subservice.setSubserviceId(3L);
        subservice.setDurationMinutes(30);

        BusinessSchedule businessSchedule = new BusinessSchedule();
        businessSchedule.updateTimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0));

        BusinessSchedule businessSchedule2 = new BusinessSchedule();
        businessSchedule2.updateTimeRange(LocalTime.of(16, 0), LocalTime.of(20, 0));

        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(3L)).thenReturn(Optional.of(subservice));
        when(businessScheduleRepository.findAllByBusiness_businessIdAndDayOfWeek(eq(10L), any(Integer.class)))
                .thenReturn(List.of(businessSchedule, businessSchedule2));
        when(bookingRepository.findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
                eq(2L), any(), any()))
                .thenReturn(List.of());

        List<AvailableSlotDTO> result = bookingService.getAvailableSlots(request);

        assertThat(result).hasSize(16);
    }

    @Test
    void shouldReturnAvailableSlotsWhenThereIsOneBooking() {
        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setCustomerUserId(1L);
        request.setEmployeeId(2L);
        request.setSubserviceId(3L);
        request.setDate(LocalDate.of(2026, 4, 1));

        Employee employee = new Employee();
        employee.setEmployeeId(2L);

        Business business = new Business();
        business.setBusinessId(10L);

        CountryTimezoneId countryTimezoneId = new CountryTimezoneId("ES","Europe/Madrid");

        CountryTimezone countryTimezone = new CountryTimezone();
        countryTimezone.setId(countryTimezoneId);

        business.setCountryTimezone(countryTimezone);
        employee.setBusiness(business);

        Subservice subservice = new Subservice();
        subservice.setSubserviceId(3L);
        subservice.setDurationMinutes(30);

        BusinessSchedule businessSchedule = new BusinessSchedule();
        businessSchedule.updateTimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0));

        //De momento no se usa
        BusinessSchedule businessSchedule2 = new BusinessSchedule();
        businessSchedule2.updateTimeRange(LocalTime.of(16, 0), LocalTime.of(20, 0));

        OffsetDateTime start = OffsetDateTime.parse("2026-04-01T11:00:00+02:00");
        OffsetDateTime end = OffsetDateTime.parse("2026-04-01T11:30:00+02:00");

        Booking booking = new Booking();
        booking.setStartDatetime(start);
        booking.setEndDatetime(end);



        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(3L)).thenReturn(Optional.of(subservice));
        when(businessScheduleRepository.findAllByBusiness_businessIdAndDayOfWeek(eq(10L), any(Integer.class)))
                .thenReturn(List.of(businessSchedule));
        when(bookingRepository.findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
                eq(2L), any(), any()))
                .thenReturn(List.of(booking));

        List<AvailableSlotDTO> result = bookingService.getAvailableSlots(request);

        assertThat(result).hasSize(7);
    }
    @Test
    void shouldReturnAvailableSlotsWhenThereIsOneBookingAtTheStartBusinessSchedule() {
        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setCustomerUserId(1L);
        request.setEmployeeId(2L);
        request.setSubserviceId(3L);
        request.setDate(LocalDate.of(2026, 4, 1));

        Employee employee = new Employee();
        employee.setEmployeeId(2L);

        Business business = new Business();
        business.setBusinessId(10L);

        CountryTimezoneId countryTimezoneId = new CountryTimezoneId("ES","Europe/Madrid");

        CountryTimezone countryTimezone = new CountryTimezone();
        countryTimezone.setId(countryTimezoneId);

        business.setCountryTimezone(countryTimezone);
        employee.setBusiness(business);

        Subservice subservice = new Subservice();
        subservice.setSubserviceId(3L);
        subservice.setDurationMinutes(30);

        BusinessSchedule businessSchedule = new BusinessSchedule();
        businessSchedule.updateTimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0));

        /*
        Para crear el horario de Tarde.
        BusinessSchedule businessSchedule2 = new BusinessSchedule();
        businessSchedule2.updateTimeRange(LocalTime.of(16, 0), LocalTime.of(20, 0));
        */
        OffsetDateTime start = OffsetDateTime.parse("2026-04-01T10:00:00+02:00");
        OffsetDateTime end = OffsetDateTime.parse("2026-04-01T10:30:00+02:00");

        Booking booking = new Booking();
        booking.setStartDatetime(start);
        booking.setEndDatetime(end);



        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(3L)).thenReturn(Optional.of(subservice));
        when(businessScheduleRepository.findAllByBusiness_businessIdAndDayOfWeek(eq(10L), any(Integer.class)))
                .thenReturn(List.of(businessSchedule));
        when(bookingRepository.findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
                eq(2L), any(), any()))
                .thenReturn(List.of(booking));

        List<AvailableSlotDTO> result = bookingService.getAvailableSlots(request);

        assertThat(result).hasSize(7);
    }
    @Test
    void shouldReturnAvailableSlotsWhenThereIsOneBookingAtTheEndBusinessSchedule() {
        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setCustomerUserId(1L);
        request.setEmployeeId(2L);
        request.setSubserviceId(3L);
        request.setDate(LocalDate.of(2026, 4, 1));

        Employee employee = new Employee();
        employee.setEmployeeId(2L);

        Business business = new Business();
        business.setBusinessId(10L);

        CountryTimezoneId countryTimezoneId = new CountryTimezoneId("ES","Europe/Madrid");

        CountryTimezone countryTimezone = new CountryTimezone();
        countryTimezone.setId(countryTimezoneId);

        business.setCountryTimezone(countryTimezone);
        employee.setBusiness(business);

        Subservice subservice = new Subservice();
        subservice.setSubserviceId(3L);
        subservice.setDurationMinutes(30);

        BusinessSchedule businessSchedule = new BusinessSchedule();
        businessSchedule.updateTimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0));

        /*
        Para crear el horario de Tarde.
        BusinessSchedule businessSchedule2 = new BusinessSchedule();
        businessSchedule2.updateTimeRange(LocalTime.of(16, 0), LocalTime.of(20, 0));
        */
        OffsetDateTime start = OffsetDateTime.parse("2026-04-01T13:30:00+02:00");
        OffsetDateTime end = OffsetDateTime.parse("2026-04-01T14:00:00+02:00");

        Booking booking = new Booking();
        booking.setStartDatetime(start);
        booking.setEndDatetime(end);



        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(3L)).thenReturn(Optional.of(subservice));
        when(employeeScheduleRepository.existsById(2L)).thenReturn(false);
        when(businessScheduleRepository.findAllByBusiness_businessIdAndDayOfWeek(eq(10L), any(Integer.class)))
                .thenReturn(List.of(businessSchedule));
        when(bookingRepository.findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
                eq(2L), any(), any()))
                .thenReturn(List.of(booking));

        List<AvailableSlotDTO> result = bookingService.getAvailableSlots(request);

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
        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setCustomerUserId(1L);
        request.setEmployeeId(2L);
        request.setSubserviceId(3L);
        request.setDate(LocalDate.of(2026, 4, 1));

        Employee employee = new Employee();
        employee.setEmployeeId(2L);

        Business business = new Business();
        business.setBusinessId(10L);

        CountryTimezoneId countryTimezoneId = new CountryTimezoneId("ES","Europe/Madrid");

        CountryTimezone countryTimezone = new CountryTimezone();
        countryTimezone.setId(countryTimezoneId);

        business.setCountryTimezone(countryTimezone);
        employee.setBusiness(business);

        Subservice subservice = new Subservice();
        subservice.setSubserviceId(3L);
        subservice.setDurationMinutes(30);

        BusinessSchedule businessSchedule = new BusinessSchedule();
        businessSchedule.updateTimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0));

        //De momento no se usa
        BusinessSchedule businessSchedule2 = new BusinessSchedule();
        businessSchedule2.updateTimeRange(LocalTime.of(16, 0), LocalTime.of(20, 0));

        OffsetDateTime start = OffsetDateTime.parse("2026-04-01T11:00:00+02:00");
        OffsetDateTime end = OffsetDateTime.parse("2026-04-01T11:30:00+02:00");

        Booking booking = new Booking();
        booking.setStartDatetime(start);
        booking.setEndDatetime(end);

        OffsetDateTime start2 = OffsetDateTime.parse("2026-04-01T12:00:00+02:00");
        OffsetDateTime end2 = OffsetDateTime.parse("2026-04-01T13:00:00+02:00");

        Booking booking2 = new Booking();
        booking2.setStartDatetime(start2);
        booking2.setEndDatetime(end2);


        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(3L)).thenReturn(Optional.of(subservice));
        when(businessScheduleRepository.findAllByBusiness_businessIdAndDayOfWeek(eq(10L), any(Integer.class)))
                .thenReturn(List.of(businessSchedule));
        when(bookingRepository.findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
                eq(2L), any(), any()))
                .thenReturn(List.of(booking,booking2));

        List<AvailableSlotDTO> result = bookingService.getAvailableSlots(request);

        assertThat(result).hasSize(5);
    }

    @Test
    void shouldReturnNoSlots_whenGapIsSmallerThanSubserviceDuration() {

        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setCustomerUserId(1L);
        request.setEmployeeId(2L);
        request.setSubserviceId(3L);
        request.setDate(LocalDate.of(2026, 4, 1));

        Employee employee = new Employee();
        employee.setEmployeeId(2L);

        Business business = new Business();
        business.setBusinessId(10L);

        CountryTimezoneId countryTimezoneId = new CountryTimezoneId("ES","Europe/Madrid");

        CountryTimezone countryTimezone = new CountryTimezone();
        countryTimezone.setId(countryTimezoneId);

        business.setCountryTimezone(countryTimezone);
        employee.setBusiness(business);

        Subservice subservice = new Subservice();
        subservice.setSubserviceId(3L);
        subservice.setDurationMinutes(30);

        BusinessSchedule businessSchedule = new BusinessSchedule();
        businessSchedule.updateTimeRange(LocalTime.of(10, 0), LocalTime.of(11, 0));

        // Booking que deja un hueco de 20 minutos
        OffsetDateTime start = OffsetDateTime.parse("2026-04-01T10:20:00+02:00");
        OffsetDateTime end = OffsetDateTime.parse("2026-04-01T11:00:00+02:00");

        Booking booking = new Booking();
        booking.setStartDatetime(start);
        booking.setEndDatetime(end);

        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(3L)).thenReturn(Optional.of(subservice));
        when(businessScheduleRepository.findAllByBusiness_businessIdAndDayOfWeek(eq(10L), any(Integer.class)))
                .thenReturn(List.of(businessSchedule));
        when(bookingRepository.findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
                eq(2L), any(), any()))
                .thenReturn(List.of(booking));

        // Act
        List<AvailableSlotDTO> result = bookingService.getAvailableSlots(request);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnOneSlot_whenGapIsExactlyEqualToSubserviceDuration() {

        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setCustomerUserId(1L);
        request.setEmployeeId(2L);
        request.setSubserviceId(3L);
        request.setDate(LocalDate.of(2026, 4, 1));

        Employee employee = new Employee();
        employee.setEmployeeId(2L);

        Business business = new Business();
        business.setBusinessId(10L);

        CountryTimezoneId countryTimezoneId = new CountryTimezoneId("ES", "Europe/Madrid");

        CountryTimezone countryTimezone = new CountryTimezone();
        countryTimezone.setId(countryTimezoneId);

        business.setCountryTimezone(countryTimezone);
        employee.setBusiness(business);

        Subservice subservice = new Subservice();
        subservice.setSubserviceId(3L);
        subservice.setDurationMinutes(30);

        BusinessSchedule businessSchedule = new BusinessSchedule();
        businessSchedule.updateTimeRange(LocalTime.of(10, 0), LocalTime.of(11, 0));

        OffsetDateTime start = OffsetDateTime.parse("2026-04-01T10:30:00+02:00");
        OffsetDateTime end = OffsetDateTime.parse("2026-04-01T11:00:00+02:00");

        Booking booking = new Booking();
        booking.setStartDatetime(start);
        booking.setEndDatetime(end);

        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(3L)).thenReturn(Optional.of(subservice));
        when(businessScheduleRepository.findAllByBusiness_businessIdAndDayOfWeek(eq(10L), any(Integer.class)))
                .thenReturn(List.of(businessSchedule));
        when(bookingRepository.findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
                eq(2L), any(), any()))
                .thenReturn(List.of(booking));

        List<AvailableSlotDTO> result = bookingService.getAvailableSlots(request);

        assertThat(result).hasSize(1);

        assertThat(result.get(0).getStartDatetime())
                .isEqualTo(OffsetDateTime.parse("2026-04-01T10:00:00+02:00"));
        assertThat(result.get(0).getEndDatetime())
                .isEqualTo(OffsetDateTime.parse("2026-04-01T10:30:00+02:00"));
    }

    @Test
    void shouldReturnAvailableSlotsWhenThereIsOneBookingInTwoBusinessSchedule() {
        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setCustomerUserId(1L);
        request.setEmployeeId(2L);
        request.setSubserviceId(3L);
        request.setDate(LocalDate.of(2026, 4, 1));

        Employee employee = new Employee();
        employee.setEmployeeId(2L);

        Business business = new Business();
        business.setBusinessId(10L);

        CountryTimezoneId countryTimezoneId = new CountryTimezoneId("ES","Europe/Madrid");

        CountryTimezone countryTimezone = new CountryTimezone();
        countryTimezone.setId(countryTimezoneId);

        business.setCountryTimezone(countryTimezone);
        employee.setBusiness(business);

        Subservice subservice = new Subservice();
        subservice.setSubserviceId(3L);
        subservice.setDurationMinutes(30);

        BusinessSchedule businessSchedule = new BusinessSchedule();
        businessSchedule.updateTimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0));

        BusinessSchedule businessSchedule2 = new BusinessSchedule();
        businessSchedule2.updateTimeRange(LocalTime.of(16, 0), LocalTime.of(20, 0));

        OffsetDateTime start = OffsetDateTime.parse("2026-04-01T12:30:00+02:00");
        OffsetDateTime end = OffsetDateTime.parse("2026-04-01T13:00:00+02:00");

        Booking booking = new Booking();
        booking.setStartDatetime(start);
        booking.setEndDatetime(end);

        OffsetDateTime start2 = OffsetDateTime.parse("2026-04-01T18:00:00+02:00");
        OffsetDateTime end2 = OffsetDateTime.parse("2026-04-01T19:00:00+02:00");

        Booking booking2 = new Booking();
        booking2.setStartDatetime(start2);
        booking2.setEndDatetime(end2);



        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(3L)).thenReturn(Optional.of(subservice));
        when(businessScheduleRepository.findAllByBusiness_businessIdAndDayOfWeek(eq(10L), any(Integer.class)))
                .thenReturn(List.of(businessSchedule,businessSchedule2));
        when(bookingRepository.findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
                eq(2L), any(), any()))
                .thenReturn(List.of(booking,booking2));

        List<AvailableSlotDTO> result = bookingService.getAvailableSlots(request);

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
}