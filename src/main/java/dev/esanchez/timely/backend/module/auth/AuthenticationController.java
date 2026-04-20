package dev.esanchez.timely.backend.module.auth;

import dev.esanchez.timely.backend.module.auth.dto.request.ResendCodeRequest;
import dev.esanchez.timely.backend.module.auth.dto.request.VerifyUserRequest;
import dev.esanchez.timely.backend.module.auth.dto.response.VerifyUserResponse;
import dev.esanchez.timely.backend.module.identity.dto.request.LoginUserRequest;
import dev.esanchez.timely.backend.module.identity.dto.request.RegisterUserRequest;
import dev.esanchez.timely.backend.module.identity.dto.response.LoginResponse;
import dev.esanchez.timely.backend.module.identity.User;
import dev.esanchez.timely.backend.core.security.CustomUserDetails;
import dev.esanchez.timely.backend.core.jwt.JwtService;
import dev.esanchez.timely.backend.module.identity.dto.response.RegisterUserResponse;
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
    public RegisterUserResponse register(@RequestBody RegisterUserRequest registerUserRequest) {
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
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserRequest loginUserRequest){
        //TODO:Move logic to Authentication Service
        User authenticatedUser = authenticationService.authenticate(loginUserRequest);
        CustomUserDetails customUserDetails = new CustomUserDetails(authenticatedUser);
        String jwtToken = jwtService.generateToken(customUserDetails);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public VerifyUserResponse verifyUser(@RequestBody VerifyUserRequest verifyUserRequest) {
            authenticationService.verifyUser(verifyUserRequest);
            return new VerifyUserResponse("Account Verified successfully");
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestBody ResendCodeRequest resendCodeRequest) {
        try {
            authenticationService.resendVerificationCode(resendCodeRequest.getEmail());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
