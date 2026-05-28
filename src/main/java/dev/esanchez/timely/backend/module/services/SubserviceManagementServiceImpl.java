package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.module.services.dto.request.CreateSubserviceRequest;
import dev.esanchez.timely.backend.module.services.dto.response.ServiceResponse;
import dev.esanchez.timely.backend.module.services.dto.response.SubserviceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubserviceManagementServiceImpl implements SubserviceManagementService {

    @Autowired
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
                .id(savedSubservice.getId())
                .name(savedSubservice.getName())
                .description(savedSubservice.getDescription())
                .price(savedSubservice.getPrice())
                .durationMinutes(savedSubservice.getDurationMinutes())
                .build();
    }

    @Override
    public List<SubserviceResponse> getAllSubservices(long service_id) {
        // Assuming SubserviceRepository has a method to find all subservices by service ID
        return subserviceRepository.findAllByServiceId(service_id).stream()
                .map(subservice -> SubserviceResponse.builder()
                        .id(subservice.getId())
                        .name(subservice.getName())
                        .description(subservice.getDescription())
                        .price(subservice.getPrice())
                        .durationMinutes(subservice.getDurationMinutes())
                        .build())
                .toList();
    }

    @Override
    public void assignEmployeeToSubservice() {
        // Implementation for assigning an employee to a subservice
    }
}
