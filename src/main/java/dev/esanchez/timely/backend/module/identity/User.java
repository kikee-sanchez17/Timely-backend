package dev.esanchez.timely.backend.module.identity;

import dev.esanchez.timely.backend.utilsCommon.ValidationUtils;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
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

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
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


    public User() {
    }

    public User(String email, String passwordHash, String name ,String suranme, String phoneNumber) {
        this.email = validateEmail(email);
        this.passwordHash = ValidationUtils.validateText(passwordHash,"Password hash cannot be null or blank");
        this.name = name;
        this.suranme = ValidationUtils.validateText(suranme,"Suranme");
        this.phoneNumber = phoneNumber;
        this.isActive = true;
    }

    public User(String email, String passwordHash){
        this.email = validateEmail(email);
        this.passwordHash = ValidationUtils.validateText(passwordHash,"Password hash cannot be null or blank");
        this.isActive = true;
        createdAt = OffsetDateTime.now();
    }

    public User(String name, String surname, String email, String passwordHash){
        this.name = name;
        this.suranme = surname;
        this.email = validateEmail(email);
        this.passwordHash = ValidationUtils.validateText(passwordHash,"Password hash cannot be null or blank");
        createdAt = OffsetDateTime.now();

    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getName() {
        return name;
    }

    public String getSuranme() {
        return suranme;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateSuranme(String suranme) {
        this.suranme = suranme;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateEmail(String email) {
        this.email = validateEmail(email);
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public OffsetDateTime getVerificationCodeExpiresAt() {
        return verificationCodeExpiresAt;
    }

    public void setVerificationCodeExpiresAt(OffsetDateTime verificationCodeExpiresAt) {
        this.verificationCodeExpiresAt = verificationCodeExpiresAt;
    }

    public void changePassword(String passwordHash) {
        this.passwordHash = ValidationUtils.validateText(passwordHash,"Password hash cannot be null or blank");
    }

    private String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        return email.trim().toLowerCase();
    }

}