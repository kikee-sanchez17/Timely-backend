package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.module.services.creator.ServiceCreator;
import dev.esanchez.timely.backend.module.services.dto.request.CreateServiceRequest;
import dev.esanchez.timely.backend.module.services.dto.response.ServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceManagementServiceImpl implements ServiceManagementService {

    private final ServiceCreator serviceCreator;
    private final ServiceGetter serviceGetter;

    @Override
    public void createService(CreateServiceRequest createServiceRequest) {
        serviceCreator.createService(createServiceRequest);
    }

    @Override
    public List<ServiceResponse> getAllServices() {
        return serviceGetter.getAllServices();
    }
}
