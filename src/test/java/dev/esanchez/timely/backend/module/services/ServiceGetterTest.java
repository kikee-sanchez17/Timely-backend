package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.core.security.AuthenticationFacade;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.services.dto.response.ServiceResponse;
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
class ServiceGetterTest {

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServiceGetter serviceGetter;

    @Test
    void getAllServices_returnsServiceList_whenServicesExist() {
        Business business = Business.builder()
                .businessId(1L)
                .name("Barber Shop")
                .build();

        Service service1 = Service.builder()
                .name("Hair")
                .description("Hair services")
                .isActive(true)
                .business(business)
                .build();

        Service service2 = Service.builder()
                .name("Beard")
                .description("Beard services")
                .isActive(true)
                .business(business)
                .build();

        when(authenticationFacade.getCurrentBusiness())
                .thenReturn(business);

        when(serviceRepository.findAllByBusiness(business))
                .thenReturn(Optional.of(List.of(service1, service2)));

        List<ServiceResponse> result = serviceGetter.getAllServices();

        assertThat(result).hasSize(2);

        assertThat(result)
                .extracting(ServiceResponse::name)
                .containsExactly("Hair", "Beard");

        assertThat(result)
                .extracting(ServiceResponse::description)
                .containsExactly("Hair services", "Beard services");

        assertThat(result)
                .extracting(ServiceResponse::isActive)
                .containsExactly(true, true);

        verify(authenticationFacade, times(1)).getCurrentBusiness();
        verify(serviceRepository, times(1)).findAllByBusiness(business);
    }

    @Test
    void getAllServices_throwsNotFoundException_whenNoServicesExist() {
        Business business = Business.builder()
                .businessId(1L)
                .name("Barber Shop")
                .build();

        when(authenticationFacade.getCurrentBusiness())
                .thenReturn(business);

        when(serviceRepository.findAllByBusiness(business))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceGetter.getAllServices())
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Services not found.");

        verify(authenticationFacade, times(1)).getCurrentBusiness();
        verify(serviceRepository, times(1)).findAllByBusiness(business);
    }
}