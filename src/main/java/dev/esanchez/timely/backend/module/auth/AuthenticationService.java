package dev.esanchez.timely.backend.module.auth;

import dev.esanchez.timely.backend.module.auth.dto.request.VerifyUserRequest;
import dev.esanchez.timely.backend.module.identity.dto.request.LoginUserRequest;
import dev.esanchez.timely.backend.module.identity.dto.request.RegisterUserRequest;
import dev.esanchez.timely.backend.module.identity.Role;
import dev.esanchez.timely.backend.module.identity.User;
import dev.esanchez.timely.backend.module.identity.UserRole;
import dev.esanchez.timely.backend.module.identity.UserRoleId;
import dev.esanchez.timely.backend.module.identity.RoleRepository;
import dev.esanchez.timely.backend.module.identity.UserRepository;
import dev.esanchez.timely.backend.module.identity.UserRoleRepository;
import dev.esanchez.timely.backend.core.email.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    //Creates user but it is not valid until its verified
    public User signup(RegisterUserRequest input) {
        User user = new User(input.getName(), input.getSurname(),input.getEmail(), passwordEncoder.encode(input.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(OffsetDateTime.now().plusMinutes(15));
        user.setIsActive(false);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    //Checks if user is verified, if so return user object
    public User authenticate(LoginUserRequest input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsActive()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return user;
    }

    //Function to verify the user with verification code
    public void verifyUser(VerifyUserRequest input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            //Checks if the verification code is expired
            if (user.getVerificationCodeExpiresAt().isBefore(OffsetDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                //Set Customer Role to user
                setRoleToUser(user,input,1L);
                user.setVerificationCode(null);
                user.setIsActive(true);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getIsActive()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(OffsetDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }
    //Makes the email body and send it to the user
    private void sendVerificationEmail(User user) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to Timely!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }
    //Generates the verification code
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
    //This function sets Role to User (Consider moving this function to a utility package)
    private void setRoleToUser(User user, VerifyUserRequest input,Long RoleId) {
        Role role = roleRepository.findById(RoleId).orElseThrow(() -> new RuntimeException("Role not found"));
        UserRoleId userRoleId = new UserRoleId(user.getUserId(), role.getRoleId());
        if (user.getVerificationCode().equals(input.getVerificationCode())) {
            if (!userRoleRepository.existsById(userRoleId)) {
                UserRole userRole = new UserRole(user, role);
                userRoleRepository.save(userRole);
            }
        }
    }
}
