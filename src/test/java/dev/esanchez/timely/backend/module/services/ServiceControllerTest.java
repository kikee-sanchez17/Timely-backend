package dev.esanchez.timely.backend.module.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.esanchez.timely.backend.core.globalException.customGlobalException.GlobalExceptionHandler;
import dev.esanchez.timely.backend.module.services.dto.request.CreateServiceRequest;
import dev.esanchez.timely.backend.module.services.dto.response.ServiceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ServiceManagementService serviceManagementService;

    @InjectMocks
    private ServiceController serviceController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(serviceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createService_delegatesToServiceManagementService() {
        CreateServiceRequest request = CreateServiceRequest.builder()
                .name("Hair")
                .description("Hair services")
                .build();

        serviceController.createService(request);

        verify(serviceManagementService, times(1)).createService(request);
    }

    @Test
    void createService_returns400_whenNameIsBlank() throws Exception {
        CreateServiceRequest request = CreateServiceRequest.builder()
                .name("")
                .description("Hair services")
                .build();

        mockMvc.perform(post("/api/service/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());

        verify(serviceManagementService, never()).createService(any());
    }

    @Test
    void createService_returns400_whenNameIsMissing() throws Exception {
        CreateServiceRequest request = CreateServiceRequest.builder()
                .description("Hair services")
                .build();

        mockMvc.perform(post("/api/service/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());

        verify(serviceManagementService, never()).createService(any());
    }

    @Test
    void getAllService_returns200_withServiceList() {
        List<ServiceResponse> services = List.of(
                ServiceResponse.builder()
                        .name("Hair")
                        .description("Hair services")
                        .isActive(true)
                        .build(),
                ServiceResponse.builder()
                        .name("Beard")
                        .description("Beard services")
                        .isActive(true)
                        .build()
        );

        when(serviceManagementService.getAllServices())
                .thenReturn(services);

        ResponseEntity<List<ServiceResponse>> response =
                serviceController.getAllService();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody())
                .extracting(ServiceResponse::name)
                .containsExactly("Hair", "Beard");

        verify(serviceManagementService, times(1)).getAllServices();
    }

    @Test
    void getAllService_returns200_withServiceList_usingMockMvc() throws Exception {
        List<ServiceResponse> services = List.of(
                ServiceResponse.builder()
                        .name("Hair")
                        .description("Hair services")
                        .isActive(true)
                        .build()
        );

        when(serviceManagementService.getAllServices())
                .thenReturn(services);

        mockMvc.perform(get("/api/service/getAllServices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hair"))
                .andExpect(jsonPath("$[0].description").value("Hair services"))
                .andExpect(jsonPath("$[0].isActive").value(true));

        verify(serviceManagementService, times(1)).getAllServices();
    }
}