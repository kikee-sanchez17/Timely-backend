package dev.esanchez.timely.backend.module.auth;

import dev.esanchez.timely.backend.module.auth.dto.request.ResendCodeRequest;
import dev.esanchez.timely.backend.module.auth.dto.request.VerifyUserRequest;
import dev.esanchez.timely.backend.module.identity.dto.request.LoginUserRequest;
import dev.esanchez.timely.backend.module.identity.dto.request.RegisterUserRequest;
import dev.esanchez.timely.backend.module.identity.dto.response.LoginResponse;
import dev.esanchez.timely.backend.module.identity.User;
import dev.esanchez.timely.backend.core.jwt.JwtService;
import dev.esanchez.timely.backend.module.identity.dto.response.RegisterUserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public RegisterUserResponse register(@RequestBody @Valid RegisterUserRequest registerUserRequest) {
        User registeredUser = authenticationService.signup(registerUserRequest);
        return new RegisterUserResponse(
                new RegisterUserResponse.UserResponse(
                        registeredUser.getUserId().toString(),
                        registeredUser.getEmail(),
                        registeredUser.getName(),
                        registeredUser.getSuranme(),
                        registeredUser.getIsActive()
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody @Valid LoginUserRequest loginUserRequest){
        return ResponseEntity.ok(authenticationService.authenticate(loginUserRequest));
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyUser(@RequestBody @Valid VerifyUserRequest verifyUserRequest) {
            authenticationService.verifyUser(verifyUserRequest);
            return ResponseEntity.ok().build();
    }

    @PostMapping("/resend")
    public ResponseEntity<Void> resendVerificationCode(@RequestBody @Valid ResendCodeRequest resendCodeRequest) {
            authenticationService.resendVerificationCode(resendCodeRequest.getEmail());
            return ResponseEntity.ok().build();
    }
}
