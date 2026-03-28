package dev.esanchez.timely.backend.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ServiceImageId implements Serializable {

    private Long serviceId;
    private Long imageId;

    public ServiceImageId() {
    }

    public ServiceImageId(Long serviceId, Long imageId) {
        this.serviceId = serviceId;
        this.imageId = imageId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public Long getImageId() {
        return imageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceImageId)) return false;
        ServiceImageId that = (ServiceImageId) o;
        return Objects.equals(serviceId, that.serviceId)
                && Objects.equals(imageId, that.imageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId, imageId);
    }
}