package dev.esanchez.timely.backend.module.services.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreateSubserviceRequest {

    @NotBlank
    private long service_id;
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private BigDecimal price;
    @NotBlank
    private Integer duration;

}
