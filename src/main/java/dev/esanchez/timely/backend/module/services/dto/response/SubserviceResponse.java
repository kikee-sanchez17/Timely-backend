package dev.esanchez.timely.backend.module.services.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SubserviceResponse(
        Long subservice_id,
        String name,
        String description,
        BigDecimal price,
        Integer duration
) {
}
