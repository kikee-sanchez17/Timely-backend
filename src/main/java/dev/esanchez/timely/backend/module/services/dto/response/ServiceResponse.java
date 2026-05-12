package dev.esanchez.timely.backend.module.services.dto.response;

import lombok.Builder;

@Builder
public record ServiceResponse(
        String name,
        String description,
        boolean isActive
) {
}
