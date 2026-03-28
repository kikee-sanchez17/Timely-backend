package dev.esanchez.timely.backend.entity;

import dev.esanchez.timely.backend.util.ValidationUtils;
import jakarta.persistence.*;

@Entity
@Table(name = "subservice_images")
public class SubserviceImage {

    @EmbeddedId
    private SubserviceImageId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("subserviceId")
    @JoinColumn(name = "subservice_id", nullable = false)
    private Subservice subservice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("imageId")
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    protected SubserviceImage() {
    }

    public SubserviceImage(Subservice subservice, Image image) {
        this.subservice = ValidationUtils.requireNonNull(subservice, "Subservice cannot be null");
        this.image = ValidationUtils.requireNonNull(image, "Image cannot be null");

        this.id = new SubserviceImageId(
                subservice.getSubserviceId(),
                image.getImageId()
        );
    }

    public SubserviceImageId getId() {
        return id;
    }

    public Subservice getSubservice() {
        return subservice;
    }

    public Image getImage() {
        return image;
    }
}