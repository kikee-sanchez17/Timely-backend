package dev.esanchez.timely.backend.module.services.dto.response;

import lombok.Builder;

@Builder
public record ServiceResponse(
        Long serviceId,
        String name,
        String description,
        boolean isActive
) {
}
