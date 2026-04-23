package dev.esanchez.timely.backend.module.identity.dto.response;

import lombok.Builder;

@Builder
public record RegisterUserResponse(
        UserResponse user
) {
    @Builder
    public record UserResponse(
            String id,
            String email,
            String name,
            String surname,
            Boolean isActive
    ) {}
}
