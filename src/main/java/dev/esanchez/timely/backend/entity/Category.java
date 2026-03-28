package dev.esanchez.timely.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    public Category() {
    }

    public Category(String code, String displayName) {
        this.code = validateCode(code);
        this.displayName = validateText(displayName, "Display name");
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void updateCode(String code) {
        this.code = validateCode(code);
    }

    public void updateDisplayName(String displayName) {
        this.displayName = validateText(displayName, "Display name");
    }

    private String validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code cannot be null or blank");
        }
        return code.trim().toUpperCase();
    }

    private String validateText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be null or blank");
        }
        return value.trim();
    }
}