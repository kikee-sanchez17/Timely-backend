package dev.esanchez.timely.backend.module.services.creator;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.core.security.AuthenticationFacade;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.services.Service;
import dev.esanchez.timely.backend.module.services.ServiceRepository;
import dev.esanchez.timely.backend.module.services.Subservice;
import dev.esanchez.timely.backend.module.services.SubserviceRepository;
import dev.esanchez.timely.backend.module.services.dto.request.CreateSubserviceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubserviceCreator {

    private final SubserviceRepository subserviceRepository;
    private final ServiceRepository serviceRepository;

    public void createSubservice (CreateSubserviceRequest createSubserviceRequest){

        Subservice subservice = Subservice.builder()
                .name(createSubserviceRequest.getName())
                .description(createSubserviceRequest.getDescription())
                .price(createSubserviceRequest.getPrice())
                .durationMinutes(createSubserviceRequest.getDuration())
                .service(getServiceByID(createSubserviceRequest.getService_id()))
                .build();

        subserviceRepository.save(subservice);

    }

    private Service getServiceByID(Long id){
        return serviceRepository.findById(id).orElseThrow(()-> new NotFoundException("Service"));
    }



}
