package dev.esanchez.timely.backend.module.booking.creator;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.core.handleException.customHandleException.CustomerNotAuthenticatedException;
import dev.esanchez.timely.backend.module.booking.Booking;
import dev.esanchez.timely.backend.module.booking.BookingRepository;
import dev.esanchez.timely.backend.module.booking.dto.request.CreateBookingRequest;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.employee.EmployeeRepository;
import dev.esanchez.timely.backend.module.identity.User;
import dev.esanchez.timely.backend.module.identity.UserRepository;
import dev.esanchez.timely.backend.module.services.Subservice;
import dev.esanchez.timely.backend.module.services.SubserviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingCreatorTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SubserviceRepository subserviceRepository;

    @InjectMocks
    private BookingCreator bookingCreator;

    @Test
    void shouldThrowWhenCustomerDoesNotExist() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setCustomerUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingCreator.create(request))
                .isInstanceOf(CustomerNotAuthenticatedException.class);
    }

    @Test
    void shouldThrowWhenEmployeeDoesNotExist() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setCustomerUserId(1L);
        request.setEmployeeId(2L);

        User user = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingCreator.create(request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowWhenSubserviceDoesNotExist() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setCustomerUserId(1L);
        request.setEmployeeId(2L);
        request.setSubserviceId(3L);

        User user = new User();
        Employee employee = new Employee();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingCreator.create(request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldCreateBookingSuccessfully() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setCustomerUserId(1L);
        request.setEmployeeId(2L);
        request.setSubserviceId(3L);
        request.setStartDatetime(OffsetDateTime.parse("2026-04-24T10:00:00+02:00"));
        request.setEndDatetime(OffsetDateTime.parse("2026-04-24T10:30:00+02:00"));
        request.setNotes("test");

        User user = new User();
        Employee employee = new Employee();
        Subservice subservice = new Subservice();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(3L)).thenReturn(Optional.of(subservice));
        when(bookingRepository.save(org.mockito.ArgumentMatchers.any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingCreator.create(request);

        assertThat(result.getCustomerUser()).isSameAs(user);
        assertThat(result.getEmployee()).isSameAs(employee);
        assertThat(result.getSubservice()).isSameAs(subservice);
        assertThat(result.getStartDatetime()).isEqualTo(request.getStartDatetime());
        assertThat(result.getEndDatetime()).isEqualTo(request.getEndDatetime());
        assertThat(result.getNotes()).isEqualTo("test");
    }
}