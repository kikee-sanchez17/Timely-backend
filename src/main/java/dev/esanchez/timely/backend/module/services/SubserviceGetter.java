package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.services.Subservice;
import dev.esanchez.timely.backend.module.services.SubserviceRepository;
import dev.esanchez.timely.backend.module.services.dto.response.SubserviceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubserviceGetter {

    private final AuthenticationFacade authenticationFacade;
    private final SubserviceRepository subserviceRepository;

    public List<SubserviceResponse> getAllSubservices(long service_id) {
        Business business = authenticationFacade.getCurrentBusiness();

        return subserviceRepository.findAllByServiceId(service_id).orElseThrow(() -> new NotFoundException("Subservices"))
                .stream()
                .map(subservice -> SubserviceResponse.builder()
                        .id(subservice.getId())
                        .name(subservice.getName())
                        .description(subservice.getDescription())
                        .price(subservice.getPrice())
                        .durationMinutes(subservice.getDurationMinutes())
                        .build())
                .toList();
    }
}
