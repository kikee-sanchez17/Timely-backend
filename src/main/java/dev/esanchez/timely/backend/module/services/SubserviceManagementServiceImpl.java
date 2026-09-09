package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.module.services.dto.request.CreateSubserviceRequest;
import dev.esanchez.timely.backend.module.services.dto.response.SubserviceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubserviceManagementServiceImpl implements SubserviceManagementService {

    private SubserviceRepository subserviceRepository;

    @Override
    public SubserviceResponse createSubservice(CreateSubserviceRequest createSubserviceRequest) {
        // Assuming Subservice is an entity and we are saving it to the database
        Subservice subservice = Subservice.builder()
                .name(createSubserviceRequest.getName())
                .description(createSubserviceRequest.getDescription())
                .price(createSubserviceRequest.getPrice())
                .durationMinutes(createSubserviceRequest.getDuration())
                .build();

        Subservice savedSubservice = subserviceRepository.save(subservice);

        return SubserviceResponse.builder()
                .subserviceId(savedSubservice.getSubserviceId())
                .name(savedSubservice.getName())
                .description(savedSubservice.getDescription())
                .price(savedSubservice.getPrice())
                .duration(savedSubservice.getDurationMinutes())
                .build();
    }

    @Override
    public List<SubserviceResponse> getAllSubservices(long service_id) {
        // Assuming SubserviceRepository has a method to find all subservices by service ID
        return subserviceRepository.findAllByServiceId(service_id).orElseThrow(() -> new NotFoundException("Subservices"))
                .stream()
                .map(subservice -> SubserviceResponse.builder()
                        .subserviceId(subservice.getSubserviceId())
                        .name(subservice.getName())
                        .description(subservice.getDescription())
                        .price(subservice.getPrice())
                        .duration(subservice.getDurationMinutes())
                        .build())
                .toList();
    }

    @Override
    public void assignEmployeeToSubservice() {
        // Implementation for assigning an employee to a subservice
    }
}
