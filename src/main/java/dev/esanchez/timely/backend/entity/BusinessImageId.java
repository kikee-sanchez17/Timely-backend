package dev.esanchez.timely.backend.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BusinessImageId implements Serializable {

    private Long businessId;
    private Long imageId;

    public BusinessImageId() {
    }

    public BusinessImageId(Long businessId, Long imageId) {
        this.businessId = businessId;
        this.imageId = imageId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public Long getImageId() {
        return imageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusinessImageId)) return false;
        BusinessImageId that = (BusinessImageId) o;
        return Objects.equals(businessId, that.businessId) &&
                Objects.equals(imageId, that.imageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(businessId, imageId);
    }
}