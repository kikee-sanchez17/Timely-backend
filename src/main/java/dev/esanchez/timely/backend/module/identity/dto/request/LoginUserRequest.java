package dev.esanchez.timely.backend.module.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginUserRequest {
    @NotBlank (message = "Email can not be blank.")
    @Email (message = "Email is not valid.")
    private String email;

    @NotBlank (message = "Password can not be blank.")
    @Size(min = 7, max = 20)
    private String password;

}
