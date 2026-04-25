package dev.esanchez.timely.backend.module.employee;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.business.BusinessRepository;
import dev.esanchez.timely.backend.module.employee.dto.response.EmployeeResponse;
import dev.esanchez.timely.backend.module.identity.User;
import dev.esanchez.timely.backend.module.identity.UserRepository;
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
    private UserRepository userRepository;

    @Mock
    private BusinessRepository businessRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeGetter employeeGetter;

    private static final String OWNER_EMAIL = "owner@example.com";

    // -------------------------------------------------------------------------
    // Happy path
    // -------------------------------------------------------------------------

    @Test
    void getAllEmployees_returnsEmployeeList_whenAllDataExists() {
        // given
        User user = User.builder().email(OWNER_EMAIL).build();
        Business business = Business.builder().user(user).build();

        List<Employee> employees = List.of(
                Employee.builder().name("John").surname("Doe").business(business).build(),
                Employee.builder().name("Jane").surname("Smith").business(business).build()
        );

        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(user));
        when(businessRepository.findByUser(user)).thenReturn(Optional.of(business));
        when(employeeRepository.findAllByBusiness(business)).thenReturn(Optional.of(employees));

        // when
        List<EmployeeResponse> result = employeeGetter.getAllEmployees(OWNER_EMAIL);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(EmployeeResponse::getName).containsExactly("John", "Jane");
        assertThat(result).extracting(EmployeeResponse::getSurname).containsExactly("Doe", "Smith");
    }

    // -------------------------------------------------------------------------
    // User not found
    // -------------------------------------------------------------------------

    @Test
    void getAllEmployees_throwsNotFoundException_whenUserNotFound() {
        // given
        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> employeeGetter.getAllEmployees(OWNER_EMAIL))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found.");

        verify(businessRepository, never()).findByUser(any());
        verify(employeeRepository, never()).findAllByBusiness(any());
    }

    // -------------------------------------------------------------------------
    // Business not found
    // -------------------------------------------------------------------------

    @Test
    void getAllEmployees_throwsNotFoundException_whenBusinessNotFound() {
        // given
        User user = User.builder().email(OWNER_EMAIL).build();

        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(user));
        when(businessRepository.findByUser(user)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> employeeGetter.getAllEmployees(OWNER_EMAIL))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Business not found.");

        verify(employeeRepository, never()).findAllByBusiness(any());
    }

    // -------------------------------------------------------------------------
    // Employees not found
    // -------------------------------------------------------------------------

    @Test
    void getAllEmployees_throwsNotFoundException_whenNoEmployeesExist() {
        // given
        User user = User.builder().email(OWNER_EMAIL).build();
        Business business = Business.builder().user(user).build();

        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(user));
        when(businessRepository.findByUser(user)).thenReturn(Optional.of(business));
        when(employeeRepository.findAllByBusiness(business)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> employeeGetter.getAllEmployees(OWNER_EMAIL))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Employees not found.");
    }

    // -------------------------------------------------------------------------
    // Interaction verification
    // -------------------------------------------------------------------------

    @Test
    void getAllEmployees_callsRepositoriesExactlyOnce_whenRequestIsValid() {
        // given
        User user = User.builder().email(OWNER_EMAIL).build();
        Business business = Business.builder().user(user).build();

        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(user));
        when(businessRepository.findByUser(user)).thenReturn(Optional.of(business));
        when(employeeRepository.findAllByBusiness(business)).thenReturn(Optional.of(List.of()));

        // when
        employeeGetter.getAllEmployees(OWNER_EMAIL);

        // then
        verify(userRepository, times(1)).findByEmail(OWNER_EMAIL);
        verify(businessRepository, times(1)).findByUser(user);
        verify(employeeRepository, times(1)).findAllByBusiness(business);
    }
}