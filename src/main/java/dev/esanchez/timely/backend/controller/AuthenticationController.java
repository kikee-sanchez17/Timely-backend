package dev.esanchez.timely.backend.controller;

import dev.esanchez.timely.backend.dto.request.LoginUserRequest;
import dev.esanchez.timely.backend.dto.request.RegisterUserRequest;
import dev.esanchez.timely.backend.dto.request.VerifyUserRequest;
import dev.esanchez.timely.backend.dto.response.LoginResponse;
import dev.esanchez.timely.backend.entity.User;
import dev.esanchez.timely.backend.security.AuthenticationService;
import dev.esanchez.timely.backend.security.CustomUserDetails;
import dev.esanchez.timely.backend.security.JwtService;
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
    public ResponseEntity<User> register(@RequestBody RegisterUserRequest registerUserRequest) {
        User registeredUser = authenticationService.signup(registerUserRequest);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserRequest loginUserRequest){
        User authenticatedUser = authenticationService.authenticate(loginUserRequest);
        CustomUserDetails customUserDetails = new CustomUserDetails(authenticatedUser);
        String jwtToken = jwtService.generateToken(customUserDetails);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserRequest verifyUserRequest) {
        try {
            authenticationService.verifyUser(verifyUserRequest);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
