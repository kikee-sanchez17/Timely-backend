package dev.esanchez.timely.backend.module.employee;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.core.security.AuthenticationFacade;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.employee.dto.response.EmployeeResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeGetterTest {

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeGetter employeeGetter;

    @Test
    void getAllEmployees_returnsEmployeeList_whenBusinessExists() {
        Business business = Business.builder()
                .businessId(1L)
                .name("Barber Shop")
                .build();

        List<Employee> employees = List.of(
                Employee.builder()
                        .name("John")
                        .surname("Doe")
                        .business(business)
                        .build(),
                Employee.builder()
                        .name("Jane")
                        .surname("Smith")
                        .business(business)
                        .build()
        );

        when(authenticationFacade.getCurrentBusiness())
                .thenReturn(business);

        when(employeeRepository.findAllByBusiness(business))
                .thenReturn(Optional.of(employees));

        List<EmployeeResponse> result = employeeGetter.getAllEmployees();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(EmployeeResponse::getName)
                .containsExactly("John", "Jane");
        assertThat(result)
                .extracting(EmployeeResponse::getSurname)
                .containsExactly("Doe", "Smith");

        verify(authenticationFacade, times(1)).getCurrentBusiness();
        verify(employeeRepository, times(1)).findAllByBusiness(business);
    }

    @Test
    void getAllEmployees_throwsNotFoundException_whenBusinessNotFound() {
        when(authenticationFacade.getCurrentBusiness())
                .thenThrow(new NotFoundException("Business"));

        assertThatThrownBy(() -> employeeGetter.getAllEmployees())
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Business not found.");

        verify(employeeRepository, never()).findAllByBusiness(any());
    }

    @Test
    void getAllEmployees_throwsNotFoundException_whenNoEmployeesExist() {
        Business business = Business.builder()
                .businessId(1L)
                .name("Barber Shop")
                .build();

        when(authenticationFacade.getCurrentBusiness())
                .thenReturn(business);

        when(employeeRepository.findAllByBusiness(business))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeGetter.getAllEmployees())
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Employees not found.");

        verify(authenticationFacade, times(1)).getCurrentBusiness();
        verify(employeeRepository, times(1)).findAllByBusiness(business);
    }

    @Test
    void getAllEmployees_callsDependenciesExactlyOnce_whenRequestIsValid() {
        Business business = Business.builder()
                .businessId(1L)
                .name("Barber Shop")
                .build();

        when(authenticationFacade.getCurrentBusiness())
                .thenReturn(business);

        when(employeeRepository.findAllByBusiness(business))
                .thenReturn(Optional.of(List.of()));

        employeeGetter.getAllEmployees();

        verify(authenticationFacade, times(1)).getCurrentBusiness();
        verify(employeeRepository, times(1)).findAllByBusiness(business);
    }
}