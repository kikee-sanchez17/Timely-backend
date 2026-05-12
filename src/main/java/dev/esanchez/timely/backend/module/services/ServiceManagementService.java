package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.module.services.dto.request.CreateServiceRequest;
import dev.esanchez.timely.backend.module.services.dto.response.ServiceResponse;

import java.util.List;

public interface ServiceManagementService {

    void createService(CreateServiceRequest createServiceRequest);
    List<ServiceResponse> getAllServices();
}
