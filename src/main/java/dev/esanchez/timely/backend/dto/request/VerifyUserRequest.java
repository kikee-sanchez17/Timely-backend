package dev.esanchez.timely.backend.dto.request;

public class VerifyUserRequest {

    private String email;
    private String verificationCode;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getVerificationCode() {
        return verificationCode;
    }
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
