package dev.esanchez.timely.backend.module.services.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SubserviceResponse(
        Long subserviceId,
        String name,
        String description,
        BigDecimal price,
        Integer duration
) {
}
