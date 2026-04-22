package dev.esanchez.timely.backend.module.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.esanchez.timely.backend.core.jwt.JwtService;
import dev.esanchez.timely.backend.module.auth.dto.request.ResendCodeRequest;
import dev.esanchez.timely.backend.module.auth.dto.request.VerifyUserRequest;
import dev.esanchez.timely.backend.module.identity.User;
import dev.esanchez.timely.backend.module.identity.dto.request.LoginUserRequest;
import dev.esanchez.timely.backend.module.identity.dto.request.RegisterUserRequest;
import dev.esanchez.timely.backend.module.identity.dto.response.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ValidationAutoConfiguration.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("plain-password");
        request.setName("Enric");
        request.setSurname("Sanchez");

        User user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setName("Enric");
        user.setSuranme("Sanchez");
        user.setIsActive(false);

        when(authenticationService.signup(any(RegisterUserRequest.class))).thenReturn(user);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value("1"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.name").value("Enric"))
                .andExpect(jsonPath("$.user.suranme").value("Sanchez"))
                .andExpect(jsonPath("$.user.isActive").value(false));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("plain-password");

        LoginResponse response = new LoginResponse();
        response.setToken("jwt-token");
        response.setExpiresIn(3600000L);

        when(authenticationService.authenticate(any(LoginUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600000L));
    }

    @Test
    void shouldVerifyUserSuccessfully() throws Exception {
        VerifyUserRequest request = new VerifyUserRequest();
        request.setEmail("test@example.com");
        request.setVerificationCode("123456");

        doNothing().when(authenticationService).verifyUser(any(VerifyUserRequest.class));

        mockMvc.perform(post("/auth/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void shouldResendVerificationCodeSuccessfully() throws Exception {
        ResendCodeRequest request = new ResendCodeRequest();
        request.setEmail("test@example.com");

        doNothing().when(authenticationService).resendVerificationCode("test@example.com");

        mockMvc.perform(post("/auth/resend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}