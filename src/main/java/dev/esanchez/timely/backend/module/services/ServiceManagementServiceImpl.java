package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.core.security.AuthenticationFacade;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.services.dto.request.CreateServiceRequest;
import dev.esanchez.timely.backend.module.services.dto.response.ServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceManagementServiceImpl implements ServiceManagementService {

    private final ServiceRepository serviceRepository;
    private final AuthenticationFacade authenticationFacade;
    @Override
    public void createService(CreateServiceRequest createServiceRequest) {

        Business business = authenticationFacade.getCurrentBusiness();

        dev.esanchez.timely.backend.module.services.Service service = dev.esanchez.timely.backend.module.services.Service.builder()
                .name(createServiceRequest.getName())
                .description(createServiceRequest.getDescription())
                .business(business)
                .isActive(true)
                .build();

        serviceRepository.save(service);
    }

    @Override
    public List<ServiceResponse> getAllServices() {
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
