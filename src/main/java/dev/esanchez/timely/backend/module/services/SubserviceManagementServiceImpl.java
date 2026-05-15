package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.module.services.creator.SubserviceCreator;
import dev.esanchez.timely.backend.module.services.dto.request.CreateSubserviceRequest;
import dev.esanchez.timely.backend.module.services.dto.response.ServiceResponse;
import dev.esanchez.timely.backend.module.services.dto.response.SubserviceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubserviceManagementServiceImpl implements SubserviceManagementService {

    private final SubserviceGetter subserviceGetter;
    private final SubserviceCreator subserviceCreator;

    @Override
    public void createSubservice(CreateSubserviceRequest createSubserviceRequest) {
        subserviceCreator.createSubservice(createSubserviceRequest);
    }

    @Override
    public List<SubserviceResponse> getAllSubservices(long service_id) {
        return subserviceGetter.getAllSubservices(service_id);
    }


    @Override
    public void assignEmployeeToSubservice() {

    }
}
