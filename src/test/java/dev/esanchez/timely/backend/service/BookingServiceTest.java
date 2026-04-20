package dev.esanchez.timely.backend.service;

import dev.esanchez.timely.backend.module.booking.BookingRepository;
import dev.esanchez.timely.backend.module.booking.dto.request.AvailableSlotRequest;
import dev.esanchez.timely.backend.module.booking.dto.response.AvailableSlotDTO;
import dev.esanchez.timely.backend.module.booking.Booking;
import dev.esanchez.timely.backend.module.booking.BookingService;
import dev.esanchez.timely.backend.module.location.CountryTimezone;
import dev.esanchez.timely.backend.module.location.CountryTimezoneId;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.business.BusinessSchedule;
import dev.esanchez.timely.backend.module.business.BusinessScheduleRepository;
import dev.esanchez.timely.backend.module.shared.ExceptionIntervalType;
import dev.esanchez.timely.backend.module.business.business.exception.BusinessExceptionInterval;
import dev.esanchez.timely.backend.module.business.exception.BusinessExceptionIntervalRepository;
import dev.esanchez.timely.backend.module.business.exception.BusinessScheduleExceptionRepository;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.employee.EmployeeRepository;
import dev.esanchez.timely.backend.module.employee.EmployeeSchedule;
import dev.esanchez.timely.backend.module.employee.EmployeeScheduleRepository;
import dev.esanchez.timely.backend.module.employee.exception.EmployeeExceptionIntervalRepository;
import dev.esanchez.timely.backend.module.employee.exception.EmployeeScheduleExceptionRepository;
import dev.esanchez.timely.backend.module.services.Subservice;
import dev.esanchez.timely.backend.module.services.SubserviceRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Mock
    private BusinessScheduleExceptionRepository businessScheduleExceptionRepository;

    @Mock
    private EmployeeScheduleExceptionRepository employeeScheduleExceptionRepository;

    @Mock
    private BusinessExceptionIntervalRepository businessExceptionIntervalRepository;

    @Mock
    private EmployeeExceptionIntervalRepository employeeExceptionIntervalRepository;

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

        List<AvailableSlotDTO> result = bookingService.calculateAvailableSlots(request);

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
    @Test
    void shouldReturnAvailableSlotsWhenThereAreNoBookingsAndThereIsEmployeeSchedule() {
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

        EmployeeSchedule employeeSchedule = new EmployeeSchedule();
        employeeSchedule.updateTimeRange(LocalTime.of(12, 0), LocalTime.of(14, 0));

        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(3L)).thenReturn(Optional.of(subservice));
        when(employeeScheduleRepository.findAllByEmployee_employeeIdAndDayOfWeek(eq(2L), any(Integer.class)))
                .thenReturn(List.of(employeeSchedule));
        when(bookingRepository.findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
                eq(2L), any(), any()))
                .thenReturn(List.of());

        List<AvailableSlotDTO> result = bookingService.getAvailableSlots(request);

        assertThat(result).hasSize(4);
    }

    @Test
    void shouldReturnCorrectSlots_WhenThereIsABusinessException() {
        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setCustomerUserId(1L);
        request.setEmployeeId(1L);
        request.setSubserviceId(3L);
        request.setDate(LocalDate.of(2026, 4, 24));

        // --- GIVEN (Configuración del escenario) ---
        LocalDate date = LocalDate.of(2026, 4, 24);

        // 1. Setup Empleado y Negocio
        Business business = new Business();
        business.setBusinessId(10L);
        CountryTimezoneId countryTimezoneId = new CountryTimezoneId("ES","Europe/Madrid");

        CountryTimezone countryTimezone = new CountryTimezone();
        countryTimezone.setId(countryTimezoneId);

        business.setCountryTimezone(countryTimezone);

        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setBusiness(business);


        Subservice subservice = new Subservice();
        subservice.setSubserviceId(3L);
        subservice.setDurationMinutes(30);

        // 2. Setup Horario: El empleado trabaja de 09:00 a 11:00
        EmployeeSchedule employeeSchedule = new EmployeeSchedule();
        employeeSchedule.updateTimeRange(LocalTime.of(9, 0), LocalTime.of(11, 0));

        // 3. Setup Excepción: El negocio cierra de 09:30 a 10:00 (FAKE BOOKING)
        BusinessExceptionInterval exception = new BusinessExceptionInterval();
        exception.updateDate(date);

        exception.updateTimeRange(LocalTime.of(9, 30),LocalTime.of(10, 0));
        exception.updateIntervalType(ExceptionIntervalType.CLOSED_INTERVAL);

        // Mocks de comportamiento
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(3L)).thenReturn(Optional.of(subservice));
        when(employeeScheduleRepository.findAllByEmployee_employeeIdAndDayOfWeek(eq(1L), any(Integer.class)))
                .thenReturn(List.of(employeeSchedule));
        when(businessExceptionIntervalRepository.findByBusiness_BusinessIdAndDate(eq(10L), any()))
                .thenReturn(List.of(exception));
        when(bookingRepository.findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(any(), any(), any()))
                .thenReturn(List.of()); // Sin bookings reales de momento

        // --- WHEN (Ejecución) ---;
        List<AvailableSlotDTO> result = bookingService.getAvailableSlots(request);

        // --- THEN (Verificación) ---
        // Esperamos:
        // 09:00 - 09:30 (LIBRE)
        // 09:30 - 10:00 (OCUPADO POR EXCEPCIÓN)
        // 10:00 - 10:30 (LIBRE)
        // 10:30 - 11:00 (LIBRE)

        assertEquals(3, result.size(), "Debería haber exactamente 3 huecos disponibles");
        assertEquals(LocalTime.of(9, 0), result.get(0).getStartDatetime().toLocalTime());
        assertEquals(LocalTime.of(10, 0), result.get(1).getStartDatetime().toLocalTime());
        assertEquals(LocalTime.of(10, 30), result.get(2).getStartDatetime().toLocalTime());
    }

    @Test
    void shouldNotDuplicateSlotsWhenBusinessOpenIntervalOverlapsBusinessSchedule() {
        LocalDate date = LocalDate.of(2026, 4, 13); // lunes
        ZoneId zoneId = ZoneId.of("Europe/Madrid");

        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setEmployeeId(1L);
        request.setSubserviceId(2L);
        request.setDate(date);

        Business business = new Business();
        business.setBusinessId(100L);

        CountryTimezoneId countryTimezoneId = new CountryTimezoneId("ES","Europe/Madrid");


        CountryTimezone countryTimezone = new CountryTimezone();
        countryTimezone.setId(countryTimezoneId);

        business.setCountryTimezone(countryTimezone);

        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setBusiness(business);

        Subservice subservice = new Subservice();
        subservice.setSubserviceId(2L);
        subservice.setDurationMinutes(30);

        BusinessSchedule businessSchedule = new BusinessSchedule();
        businessSchedule.updateTimeRange(LocalTime.of(10, 0) , LocalTime.of(14, 0));

        BusinessExceptionInterval openInterval = new BusinessExceptionInterval();
        openInterval.updateDate(date);
        openInterval.updateTimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0));
        openInterval.updateIntervalType(ExceptionIntervalType.OPEN_INTERVAL);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(2L)).thenReturn(Optional.of(subservice));

        when(businessScheduleExceptionRepository.findByBusiness_BusinessIdAndDate(100L, date))
                .thenReturn(null);
        when(employeeScheduleExceptionRepository.findByEmployee_EmployeeIdAndDate(1L, date))
                .thenReturn(null);

        when(businessExceptionIntervalRepository.findByBusiness_BusinessIdAndDate(100L, date))
                .thenReturn(List.of(openInterval));
        when(employeeExceptionIntervalRepository.findByEmployee_EmployeeIdAndDate(1L, date))
                .thenReturn(List.of());

        when(employeeScheduleRepository.findAllByEmployee_employeeIdAndDayOfWeek(1L, date.getDayOfWeek().getValue()))
                .thenReturn(List.of());

        when(businessScheduleRepository.findAllByBusiness_businessIdAndDayOfWeek(100L, date.getDayOfWeek().getValue()))
                .thenReturn(List.of(businessSchedule));

        OffsetDateTime startOfDay = date.atStartOfDay(zoneId).toOffsetDateTime();
        OffsetDateTime startOfNextDay = date.plusDays(1).atStartOfDay(zoneId).toOffsetDateTime();

        when(bookingRepository
                .findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
                        1L, startOfDay, startOfNextDay
                ))
                .thenReturn(List.of());

        List<AvailableSlotDTO> result = bookingService.calculateAvailableSlots(request);

        assertThat(result).hasSize(8);

        assertThat(result)
                .extracting(AvailableSlotDTO::getStartDatetime)
                .containsExactly(
                        OffsetDateTime.of(date, LocalTime.of(10, 0), zoneId.getRules().getOffset(startOfDay.toInstant())),
                        OffsetDateTime.of(date, LocalTime.of(10, 30), zoneId.getRules().getOffset(startOfDay.toInstant())),
                        OffsetDateTime.of(date, LocalTime.of(11, 0), zoneId.getRules().getOffset(startOfDay.toInstant())),
                        OffsetDateTime.of(date, LocalTime.of(11, 30), zoneId.getRules().getOffset(startOfDay.toInstant())),
                        OffsetDateTime.of(date, LocalTime.of(12, 0), zoneId.getRules().getOffset(startOfDay.toInstant())),
                        OffsetDateTime.of(date, LocalTime.of(12, 30), zoneId.getRules().getOffset(startOfDay.toInstant())),
                        OffsetDateTime.of(date, LocalTime.of(13, 0), zoneId.getRules().getOffset(startOfDay.toInstant())),
                        OffsetDateTime.of(date, LocalTime.of(13, 30), zoneId.getRules().getOffset(startOfDay.toInstant()))
                );

        assertThat(result)
                .extracting(AvailableSlotDTO::getEndDatetime)
                .containsExactly(
                        OffsetDateTime.of(date, LocalTime.of(10, 30), zoneId.getRules().getOffset(startOfDay.toInstant())),
                        OffsetDateTime.of(date, LocalTime.of(11, 0), zoneId.getRules().getOffset(startOfDay.toInstant())),
                        OffsetDateTime.of(date, LocalTime.of(11, 30), zoneId.getRules().getOffset(startOfDay.toInstant())),
                        OffsetDateTime.of(date, LocalTime.of(12, 0), zoneId.getRules().getOffset(startOfDay.toInstant())),
                        OffsetDateTime.of(date, LocalTime.of(12, 30), zoneId.getRules().getOffset(startOfDay.toInstant())),
                        OffsetDateTime.of(date, LocalTime.of(13, 0), zoneId.getRules().getOffset(startOfDay.toInstant())),
                        OffsetDateTime.of(date, LocalTime.of(13, 30), zoneId.getRules().getOffset(startOfDay.toInstant())),
                        OffsetDateTime.of(date, LocalTime.of(14, 0), zoneId.getRules().getOffset(startOfDay.toInstant()))
                );
    }



}