package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.module.services.dto.response.SubserviceResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class SubserviceGetter {

    private final SubserviceRepository subserviceRepository;

    public List<SubserviceResponse> getAllSubservices(long service_id){

        return subserviceRepository.findAllByServiceId(service_id).orElseThrow(()->new NotFoundException("Subservices"))
                .stream().map(subservice -> SubserviceResponse.builder()
                    .name(subservice.getName())
                    .description(subservice.getDescription())
                    .price(subservice.getPrice())
                    .duration(subservice.getDurationMinutes()
                    ).build()
                    ).toList();
    }
}
