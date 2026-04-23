package dev.esanchez.timely.backend.module.business;

import dev.esanchez.timely.backend.module.business.dto.request.CreateBusinessRequest;
public interface BusinessService {

    String createBusiness(CreateBusinessRequest createBusinessRequest, String email);


}
