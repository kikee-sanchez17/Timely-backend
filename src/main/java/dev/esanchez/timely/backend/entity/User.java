package dev.esanchez.timely.backend.entity;

import dev.esanchez.timely.backend.util.ValidationUtils;
import jakarta.persistence.*;

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

    public User() {
    }

    public User(String email, String passwordHash) {
        this.email = validateEmail(email);
        this.passwordHash = ValidationUtils.validateText(passwordHash,"Password hash cannot be null or blank");
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

    public void updateEmail(String email) {
        this.email = validateEmail(email);
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