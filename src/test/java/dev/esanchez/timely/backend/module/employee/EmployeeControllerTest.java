package dev.esanchez.timely.backend.module.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.esanchez.timely.backend.core.globalException.customGlobalException.GlobalExceptionHandler;
import dev.esanchez.timely.backend.module.employee.dto.request.CreateEmployeeRequest;
import dev.esanchez.timely.backend.module.employee.dto.response.EmployeeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createEmployee_delegatesToService() {
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .name("John")
                .surname("Doe")
                .build();

        employeeController.createEmployee(request);

        verify(employeeService, times(1)).createEmployee(request);
    }

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

        verify(employeeService, never()).createEmployee(any());
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

        verify(employeeService, never()).createEmployee(any());
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

        verify(employeeService, never()).createEmployee(any());
    }

    @Test
    void getAllEmployees_returns200_withEmployeeList() {
        List<EmployeeResponse> employees = List.of(
                EmployeeResponse.builder().name("John").surname("Doe").build(),
                EmployeeResponse.builder().name("Jane").surname("Smith").build()
        );

        when(employeeService.getAllEmployees()).thenReturn(employees);

        ResponseEntity<List<EmployeeResponse>> response =
                employeeController.getAllEmployees();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody())
                .extracting(EmployeeResponse::getName)
                .containsExactly("John", "Jane");

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void getAllEmployees_returns200_withEmptyList() {
        when(employeeService.getAllEmployees()).thenReturn(List.of());

        ResponseEntity<List<EmployeeResponse>> response =
                employeeController.getAllEmployees();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();

        verify(employeeService, times(1)).getAllEmployees();
    }
}