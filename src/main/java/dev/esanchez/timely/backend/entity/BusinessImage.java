package dev.esanchez.timely.backend.entity;

import dev.esanchez.timely.backend.util.ValidationUtils;
import jakarta.persistence.*;

@Entity
@Table(name = "business_images")
public class BusinessImage {

    @EmbeddedId
    private BusinessImageId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("businessId")
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("imageId")
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    protected BusinessImage() {
    }

    public BusinessImage(Business business, Image image) {
        this.business = ValidationUtils.requireNonNull(business,"Business");
        this.image = ValidationUtils.requireNonNull(image,"Image");
        this.id = new BusinessImageId(
                business.getBusinessId(),
                image.getImageId()
        );
    }

    public Business getBusiness() {
        return business;
    }

    public Image getImage() {
        return image;
    }

    public BusinessImageId getId() {
        return id;
    }
}