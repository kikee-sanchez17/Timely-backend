package dev.esanchez.timely.backend.entity;

import dev.esanchez.timely.backend.util.ValidationUtils;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Long imageId;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "alt_text")
    private String altText;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Image() {
    }

    public Image(String path,
                 String altText,
                 User uploadedBy) {

        this.path = ValidationUtils.validateText(path, "Path");
        this.altText = ValidationUtils.normalizeOptionalText(altText);
        this.uploadedBy = ValidationUtils.requireNonNull(uploadedBy, "User cannot be null");
    }

    // Getters

    public Long getImageId() {
        return imageId;
    }

    public String getPath() {
        return path;
    }

    public String getAltText() {
        return altText;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    // Domain Methods

    public void updateAltText(String altText) {
        this.altText = ValidationUtils.normalizeOptionalText(altText);
    }

    public void updatePath(String path) {
        this.path = ValidationUtils.validateText(path, "Path");
    }
}