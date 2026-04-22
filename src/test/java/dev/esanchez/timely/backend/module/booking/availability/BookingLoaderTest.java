package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.module.booking.dto.request.AvailableSlotRequest;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.employee.EmployeeRepository;
import dev.esanchez.timely.backend.module.services.Subservice;
import dev.esanchez.timely.backend.module.services.SubserviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingLoaderTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SubserviceRepository subserviceRepository;

    @InjectMocks
    private BookingLoader bookingLoader;

    @Test
    void shouldThrowWhenEmployeeDoesNotExist() {
        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setEmployeeId(1L);
        request.setSubserviceId(2L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingLoader.loadAvailabilityData(request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowWhenSubserviceDoesNotExist() {
        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setEmployeeId(1L);
        request.setSubserviceId(2L);

        Employee employee = new Employee();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingLoader.loadAvailabilityData(request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldLoadAvailabilityDataSuccessfully() {
        AvailableSlotRequest request = new AvailableSlotRequest();
        request.setEmployeeId(1L);
        request.setSubserviceId(2L);

        Business business = new Business();
        Employee employee = new Employee();
        employee.setBusiness(business);

        Subservice subservice = new Subservice();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(subserviceRepository.findById(2L)).thenReturn(Optional.of(subservice));

        AvailabilityData result = bookingLoader.loadAvailabilityData(request);

        assertThat(result.employee()).isSameAs(employee);
        assertThat(result.business()).isSameAs(business);
        assertThat(result.subservice()).isSameAs(subservice);
    }
}