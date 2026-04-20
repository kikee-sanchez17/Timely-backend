package dev.esanchez.timely.backend.module.auth.dto.request;

public class ResendCodeRequest {
    private String email;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
