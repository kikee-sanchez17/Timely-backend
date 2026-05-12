package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.core.security.AuthenticationFacade;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.services.dto.response.ServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceGetter {

    private final AuthenticationFacade authenticationFacade;
    private final ServiceRepository serviceRepository;

    public List<ServiceResponse> getAllServices () {
        Business business = authenticationFacade.getCurrentBusiness();

        return serviceRepository.findAllByBusiness(business)
                .orElseThrow(() -> new NotFoundException("Services"))
                .stream()
                .map(service -> ServiceResponse.builder()
                        .name(service.getName())
                        .description(service.getDescription())
                        .isActive(service.getIsActive())
                        .build())
                .toList();

    }
}
