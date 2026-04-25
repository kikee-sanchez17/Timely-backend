package dev.esanchez.timely.backend.module.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.esanchez.timely.backend.core.globalException.customGlobalException.GlobalExceptionHandler;
import dev.esanchez.timely.backend.core.security.CustomUserDetails;
import dev.esanchez.timely.backend.module.employee.dto.request.CreateEmployeeRequest;
import dev.esanchez.timely.backend.module.employee.dto.response.EmployeeResponse;
import dev.esanchez.timely.backend.module.identity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    // MockMvc solo para tests de validación @Valid
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private static final String OWNER_EMAIL = "owner@example.com";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private CustomUserDetails mockPrincipal() {
        User owner = mock(User.class);
        when(owner.getEmail()).thenReturn(OWNER_EMAIL);
        // elimina: when(owner.getIsActive()).thenReturn(true);
        return new CustomUserDetails(owner, List.of());
    }

    // -------------------------------------------------------------------------
    // POST /api/employee/create — llamada directa al controller
    // -------------------------------------------------------------------------

    @Test
    void createEmployee_delegatesToService() {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .name("John")
                .surname("Doe")
                .build();

        employeeController.createEmployee(request, mockPrincipal());

        verify(employeeService, times(1)).createEmployee(request, OWNER_EMAIL);
    }

    @Test
    void createEmployee_delegatesEmailFromPrincipal() {
        CreateEmployeeRequest request = mock(CreateEmployeeRequest.class);

        employeeController.createEmployee(request, mockPrincipal());

        verify(employeeService).createEmployee(any(), eq(OWNER_EMAIL));
    }

    // -------------------------------------------------------------------------
    // POST /api/employee/create — validaciones @Valid via MockMvc
    // -------------------------------------------------------------------------

    @Test
    void createEmployee_returns400_whenNameIsBlank() throws Exception {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .name("")
                .surname("Doe")
                .build();

        mockMvc.perform(post("/api/employee/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());

        verify(employeeService, never()).createEmployee(any(), any());
    }

    @Test
    void createEmployee_returns400_whenSurnameIsBlank() throws Exception {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .name("John")
                .surname("")
                .build();

        mockMvc.perform(post("/api/employee/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.surname").exists());

        verify(employeeService, never()).createEmployee(any(), any());
    }

    @Test
    void createEmployee_returns400_whenBothFieldsAreMissing() throws Exception {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder().build();

        mockMvc.perform(post("/api/employee/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.surname").exists());

        verify(employeeService, never()).createEmployee(any(), any());
    }

    // -------------------------------------------------------------------------
    // POST /api/employee/getAllEmployees — llamada directa al controller
    // -------------------------------------------------------------------------

    @Test
    void getAllEmployees_returns200_withEmployeeList() {
        List<EmployeeResponse> employees = List.of(
                EmployeeResponse.builder().name("John").surname("Doe").build(),
                EmployeeResponse.builder().name("Jane").surname("Smith").build()
        );

        when(employeeService.getAllEmployees(OWNER_EMAIL)).thenReturn(employees);

        ResponseEntity<List<EmployeeResponse>> response = employeeController.getAllEmployees(mockPrincipal());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).extracting(EmployeeResponse::getName).containsExactly("John", "Jane");
        verify(employeeService, times(1)).getAllEmployees(OWNER_EMAIL);
    }

    @Test
    void getAllEmployees_returns200_withEmptyList() {
        when(employeeService.getAllEmployees(OWNER_EMAIL)).thenReturn(List.of());

        ResponseEntity<List<EmployeeResponse>> response = employeeController.getAllEmployees(mockPrincipal());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(employeeService, times(1)).getAllEmployees(OWNER_EMAIL);
    }

    @Test
    void getAllEmployees_delegatesEmailFromPrincipal() {
        employeeController.getAllEmployees(mockPrincipal());

        verify(employeeService).getAllEmployees(eq(OWNER_EMAIL));
    }
}