package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.module.services.creator.ServiceCreator;
import dev.esanchez.timely.backend.module.services.dto.request.CreateServiceRequest;
import dev.esanchez.timely.backend.module.services.dto.response.ServiceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceManagementServiceImplTest {

    @Mock
    private ServiceCreator serviceCreator;

    @Mock
    private ServiceGetter serviceGetter;

    @InjectMocks
    private ServiceManagementServiceImpl serviceManagementService;

    @Test
    void createService_delegatesToServiceCreator() {
        CreateServiceRequest request = CreateServiceRequest.builder()
                .name("Hair")
                .description("Hair services")
                .build();

        serviceManagementService.createService(request);

        verify(serviceCreator, times(1)).createService(request);
    }

    @Test
    void getAllServices_delegatesToServiceGetter() {
        List<ServiceResponse> services = List.of(
                ServiceResponse.builder()
                        .name("Hair")
                        .description("Hair services")
                        .isActive(true)
                        .build()
        );

        when(serviceGetter.getAllServices()).thenReturn(services);

        List<ServiceResponse> result = serviceManagementService.getAllServices();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Hair");

        verify(serviceGetter, times(1)).getAllServices();
    }
}