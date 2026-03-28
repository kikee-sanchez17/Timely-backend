package dev.esanchez.timely.backend.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SubserviceImageId implements Serializable {

    private Long subserviceId;
    private Long imageId;

    public SubserviceImageId() {
    }

    public SubserviceImageId(Long subserviceId, Long imageId) {
        this.subserviceId = subserviceId;
        this.imageId = imageId;
    }

    public Long getSubserviceId() {
        return subserviceId;
    }

    public Long getImageId() {
        return imageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubserviceImageId)) return false;
        SubserviceImageId that = (SubserviceImageId) o;
        return Objects.equals(subserviceId, that.subserviceId) &&
                Objects.equals(imageId, that.imageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subserviceId, imageId);
    }
}