package dev.esanchez.timely.backend.module.services.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateServiceRequest {

    @NotBlank
    private String name;

    private String description;



}
