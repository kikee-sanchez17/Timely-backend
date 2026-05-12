package dev.esanchez.timely.backend.module.services.creator;

import dev.esanchez.timely.backend.core.security.AuthenticationFacade;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.services.Service;
import dev.esanchez.timely.backend.module.services.ServiceRepository;
import dev.esanchez.timely.backend.module.services.dto.request.CreateServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceCreator {

    private final AuthenticationFacade authenticationFacade;
    private final ServiceRepository serviceRepository;

    public void createService(CreateServiceRequest createServiceRequest){

        Business business = authenticationFacade.getCurrentBusiness();

        Service service = Service.builder()
                        .name(createServiceRequest.getName())
                        .description(createServiceRequest.getDescription())
                        .business(business)
                        .isActive(true)
                        .build();

        serviceRepository.save(service);

    }
}
