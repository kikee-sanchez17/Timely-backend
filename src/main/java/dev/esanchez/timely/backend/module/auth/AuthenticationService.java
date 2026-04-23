package dev.esanchez.timely.backend.module.auth;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.CustomValidationException;
import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.core.handleException.customHandleException.AlreadyExistsException;
import dev.esanchez.timely.backend.core.handleException.customHandleException.BadRequestException;
import dev.esanchez.timely.backend.core.jwt.JwtService;
import dev.esanchez.timely.backend.core.security.CustomUserDetails;
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
import dev.esanchez.timely.backend.module.identity.dto.response.LoginResponse;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.List;
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
    private final JwtService jwtService;

    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    //Creates user but it is not valid until its verified
    public User signup(RegisterUserRequest input) {
        if(userRepository.existsByEmail(input.getEmail())) throw new AlreadyExistsException("User ");
        return userRepository.save(createUser(input));
    }

    //Checks if user is verified, if so return user object
    public LoginResponse authenticate(LoginUserRequest input) {
        User user = findByEmailOrThrow(input.getEmail());

        if (!user.getIsVerified()) throw new BadRequestException("Account not verified. Please verify your account.");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new CustomValidationException("Invalid email or password");
        } catch (DisabledException e) {
            throw new CustomValidationException("Account is disabled");
        }

        return new LoginResponse(generateJwtToken(user),jwtService.getExpirationTime());
    }

    //Function to verify the user with verification code
    public void verifyUser(VerifyUserRequest input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            //Checks if the verification code is expired
            if (user.getVerificationCodeExpiresAt().isBefore(OffsetDateTime.now())) throw new BadRequestException("Verification code has expired");

            if (checkVerificationCode(input.getVerificationCode(),user.getVerificationCode())) {
                //Set Customer Role to user
                setRoleToUser(user,1L);
                activateUser(user);
                userRepository.save(user);
            } else {
                throw new BadRequestException("Invalid verification code");
            }
        } else {
            throw new NotFoundException("User");
        }
    }

    public void resendVerificationCode(String email) {
        System.out.println("email received: " + email);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getIsVerified()) {
                throw new BadRequestException("User Already Verified.");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(OffsetDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new NotFoundException("User");
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
    private void setRoleToUser(User user,Long RoleId) {
        Role role = roleRepository.findById(RoleId).orElseThrow(() -> new NotFoundException("Role"));
        UserRoleId userRoleId = new UserRoleId(user.getUserId(), role.getRoleId());
            if (!userRoleRepository.existsById(userRoleId)) {
                UserRole userRole = new UserRole(user, role);
                userRoleRepository.save(userRole);
            }

    }

    private User createUser(RegisterUserRequest registerUserRequest) {
        User user = new User(registerUserRequest.getName(), registerUserRequest.getSurname(),registerUserRequest.getEmail(), passwordEncoder.encode(registerUserRequest.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(OffsetDateTime.now().plusMinutes(15));
        sendVerificationEmail(user);

        return user;
    }

    private void activateUser(User user) {
        user.setVerificationCode(null);
        user.setIsVerified(true);
        user.setVerificationCodeExpiresAt(null);
    }

    private boolean checkVerificationCode(String verificationCode, String verificationCodeDb) {
        return verificationCode.equals(verificationCodeDb);
    }

    private String generateJwtToken(User user) {
        List<UserRole> userRole = userRoleRepository.findAllByUser(user);
        CustomUserDetails customUserDetails = new CustomUserDetails(user,userRole);
        return  jwtService.generateToken(customUserDetails);
    }

    private User findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: ",email));
    }
}
