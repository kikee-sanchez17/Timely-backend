package dev.esanchez.timely.backend.module.identity;

import dev.esanchez.timely.backend.module.utilsCommon.ValidationUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String suranme;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_expiration")
    private OffsetDateTime verificationCodeExpiresAt;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "created_at", nullable = false,  updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column (name = "is_verified", nullable = false)
    private Boolean isVerified;


    public User(String name, String surname, String email, String passwordHash){
        this.name = name;
        this.suranme = surname;
        this.email = email;
        this.passwordHash = ValidationUtils.validateText(passwordHash,"Password hash cannot be null or blank");
        createdAt = OffsetDateTime.now();
        this.isActive = true;
        this.isVerified = false;

    }



}