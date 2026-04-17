package dev.esanchez.timely.backend.module.identity.dto.response;

public record RegisterUserResponse(
        UserResponse user
) {
    public record UserResponse(
            String id,
            String email,
            String name,
            String surname,
            Boolean isActive
    ) {}
}
