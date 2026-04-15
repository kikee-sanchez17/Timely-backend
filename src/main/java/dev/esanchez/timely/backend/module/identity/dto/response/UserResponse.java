package dev.esanchez.timely.backend.module.identity.dto.response;

public class UserResponse {

    private Long userId;
    private String email;

    public UserResponse(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}