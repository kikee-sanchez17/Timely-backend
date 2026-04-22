package dev.esanchez.timely.backend.module.auth;

import dev.esanchez.timely.backend.core.email.EmailService;
import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.core.security.CustomUserDetails;
import dev.esanchez.timely.backend.module.auth.dto.request.VerifyUserRequest;
import dev.esanchez.timely.backend.module.identity.*;
import dev.esanchez.timely.backend.core.jwt.JwtService;
import dev.esanchez.timely.backend.module.identity.dto.request.LoginUserRequest;
import dev.esanchez.timely.backend.module.identity.dto.request.RegisterUserRequest;
import dev.esanchez.timely.backend.module.identity.dto.response.LoginResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    // añade aquí más mocks si tu AuthenticationService los necesita:
     @Mock private RoleRepository roleRepository;
     @Mock private UserRoleRepository userRoleRepository;
     @Mock private EmailService emailService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("plain-password");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authenticationService.signup(request);

        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getPasswordHash()).isEqualTo("encoded-password");
        verify(passwordEncoder).encode("plain-password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenRegisteringWithExistingEmail() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("plain-password");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authenticationService.signup(request));
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("plain-password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setName("test");
        user.setSuranme("testSurname");
        user.setUserId(1L);
        user.setIsVerified(true);
        user.setIsActive(true);

        Role role = new Role();
        role.setCode("CUSTOMER");
        role.setRoleId(1L);

        List<UserRole> userRoles = new ArrayList<>();
        UserRole userRole = new UserRole(user, role);
        userRoles.add(userRole);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRoleRepository.findAllByUser(user)).thenReturn(userRoles);
        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn("jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        LoginResponse response = authenticationService.authenticate(request);

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getExpiresIn()).isEqualTo(3600000L);
    }

    @Test
    void shouldThrowWhenLoginCredentialsAreInvalid() {
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrong-password");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void shouldThrowWhenLoginUserDoesNotExistAfterAuthentication() {
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("missing@example.com");
        request.setPassword("plain-password");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void shouldVerifyUserSuccessfully() {
        VerifyUserRequest request = new VerifyUserRequest();
        request.setEmail("test@example.com");
        request.setVerificationCode("123456");

        User user = new User();
        user.setEmail("test@example.com");
        user.setVerificationCode("123456");
        user.setVerificationCodeExpiresAt(OffsetDateTime.now().plusMinutes(10));
        user.setIsActive(false);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        authenticationService.verifyUser(request);

        assertThat(user.getVerificationCode()).isNull();
        assertThat(user.getVerificationCodeExpiresAt()).isNull();
        assertThat(user.getIsActive()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowWhenVerificationCodeIsInvalid() {
        VerifyUserRequest request = new VerifyUserRequest();
        request.setEmail("test@example.com");
        request.setVerificationCode("999999");

        User user = new User();
        user.setEmail("test@example.com");
        user.setVerificationCode("123456");
        user.setVerificationCodeExpiresAt(OffsetDateTime.now().plusMinutes(10));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> authenticationService.verifyUser(request));
    }

    @Test
    void shouldThrowWhenVerificationCodeIsExpired() {
        VerifyUserRequest request = new VerifyUserRequest();
        request.setEmail("test@example.com");
        request.setVerificationCode("123456");

        User user = new User();
        user.setEmail("test@example.com");
        user.setVerificationCode("123456");
        user.setVerificationCodeExpiresAt(OffsetDateTime.now().minusMinutes(1));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> authenticationService.verifyUser(request));
    }

    @Test
    void shouldThrowWhenVerifyingNonExistingUser() {
        VerifyUserRequest request = new VerifyUserRequest();
        request.setEmail("missing@example.com");
        request.setVerificationCode("123456");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authenticationService.verifyUser(request));
    }
}