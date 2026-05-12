package dev.esanchez.timely.backend.module.employee;

import dev.esanchez.timely.backend.core.security.AuthenticationFacade;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.employee.creator.EmployeeCreator;
import dev.esanchez.timely.backend.module.employee.dto.request.CreateEmployeeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeCreatorTest {

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeCreator employeeCreator;

    // -------------------------------------------------------------------------
    // Happy path
    // -------------------------------------------------------------------------

    @Test
    void create_savesEmployeeWithCorrectData_whenBusinessExists() {

        // given
        Business business = Business.builder()
                .businessId(1L)
                .name("Barber Shop")
                .build();

        when(authenticationFacade.getCurrentBusiness())
                .thenReturn(business);

        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .name("John")
                .surname("Doe")
                .build();

        // when
        employeeCreator.create(request);

        // then
        ArgumentCaptor<Employee> captor =
                ArgumentCaptor.forClass(Employee.class);

        verify(employeeRepository).save(captor.capture());

        Employee saved = captor.getValue();

        assertThat(saved.getName()).isEqualTo("John");
        assertThat(saved.getSurname()).isEqualTo("Doe");
        assertThat(saved.getBusiness()).isEqualTo(business);
        assertThat(saved.getIsActive()).isTrue();
    }

    // -------------------------------------------------------------------------
    // Business not found
    // -------------------------------------------------------------------------

    @Test
    void create_throwsRuntimeException_whenBusinessDoesNotExist() {

        // given
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .name("John")
                .surname("Doe")
                .build();

        when(authenticationFacade.getCurrentBusiness())
                .thenThrow(new RuntimeException("Business not found"));

        // when / then
        assertThatThrownBy(() -> employeeCreator.create(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Business not found");

        verify(employeeRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // Interaction verification
    // -------------------------------------------------------------------------

    @Test
    void create_callsDependenciesOnce_whenRequestIsValid() {

        // given
        Business business = Business.builder()
                .businessId(1L)
                .name("Barber Shop")
                .build();

        when(authenticationFacade.getCurrentBusiness())
                .thenReturn(business);

        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .name("Jane")
                .surname("Smith")
                .build();

        // when
        employeeCreator.create(request);

        // then
        verify(authenticationFacade, times(1))
                .getCurrentBusiness();

        verify(employeeRepository, times(1))
                .save(any(Employee.class));
    }
}