package dev.esanchez.timely.backend.entity;

import dev.esanchez.timely.backend.util.ValidationUtils;
import jakarta.persistence.*;

@Entity
@Table(name = "service_images")
public class ServiceImage {

    @EmbeddedId
    private ServiceImageId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("serviceId")
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("imageId")
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    protected ServiceImage() {
    }

    public ServiceImage(Service service, Image image) {
        this.service = ValidationUtils.requireNonNull(service,"Service cannot be null");
        this.image = ValidationUtils.requireNonNull(image,"Image cannot be null");
        this.id = new ServiceImageId(
                service.getServiceId(),
                image.getImageId()
        );
    }

    public ServiceImageId getId() {
        return id;
    }

    public Service getService() {
        return service;
    }

    public Image getImage() {
        return image;
    }
}