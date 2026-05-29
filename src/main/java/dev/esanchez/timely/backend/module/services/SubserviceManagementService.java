package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.module.services.dto.request.CreateSubserviceRequest;
import dev.esanchez.timely.backend.module.services.dto.response.SubserviceResponse;

import java.util.List;

public interface SubserviceManagementService {

    SubserviceResponse createSubservice(CreateSubserviceRequest createSubserviceRequest);

    List<SubserviceResponse> getAllSubservices(long service_id);

    void assignEmployeeToSubservice();

}
