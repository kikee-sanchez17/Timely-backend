package dev.esanchez.timely.backend.module.employee;

import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.business.BusinessRepository;
import dev.esanchez.timely.backend.module.employee.creator.EmployeeCreator;
import dev.esanchez.timely.backend.module.employee.dto.request.CreateEmployeeRequest;
import dev.esanchez.timely.backend.module.identity.User;
import dev.esanchez.timely.backend.module.identity.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeCreatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BusinessRepository businessRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeCreator employeeCreator;

    private static final String OWNER_EMAIL = "owner@example.com";

    // -------------------------------------------------------------------------
    // Happy path
    // -------------------------------------------------------------------------

    @Test
    void create_savesEmployeeWithCorrectData_whenUserAndBusinessExist() {
        // given
        User user = User.builder().email(OWNER_EMAIL).build();
        Business business = Business.builder().user(user).build();

        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .name("John")
                .surname("Doe")
                .build();

        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(user));
        when(businessRepository.findByUser(user)).thenReturn(Optional.of(business));

        // when
        employeeCreator.create(request, OWNER_EMAIL);

        // then
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());

        Employee saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("John");
        assertThat(saved.getSurname()).isEqualTo("Doe");
        assertThat(saved.getBusiness()).isEqualTo(business);
    }

    // -------------------------------------------------------------------------
    // User not found
    // -------------------------------------------------------------------------

    @Test
    void create_throwsRuntimeException_whenUserNotFound() {
        // given
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .name("John")
                .surname("Doe")
                .build();

        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> employeeCreator.create(request, OWNER_EMAIL))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

        verify(businessRepository, never()).findByUser(any());
        verify(employeeRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // Business not found
    // -------------------------------------------------------------------------

    @Test
    void create_throwsRuntimeException_whenBusinessNotFound() {
        // given
        User user = User.builder().email(OWNER_EMAIL).build();

        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .name("John")
                .surname("Doe")
                .build();

        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(user));
        when(businessRepository.findByUser(user)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> employeeCreator.create(request, OWNER_EMAIL))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Business not found");

        verify(employeeRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // Interaction verification
    // -------------------------------------------------------------------------

    @Test
    void create_callsRepositoriesInOrder_whenRequestIsValid() {
        // given
        User user = User.builder().email(OWNER_EMAIL).build();
        Business business = Business.builder().user(user).build();

        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .name("Jane")
                .surname("Smith")
                .build();

        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(user));
        when(businessRepository.findByUser(user)).thenReturn(Optional.of(business));

        // when
        employeeCreator.create(request, OWNER_EMAIL);

        // then — each repository is called exactly once
        verify(userRepository, times(1)).findByEmail(OWNER_EMAIL);
        verify(businessRepository, times(1)).findByUser(user);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }
}