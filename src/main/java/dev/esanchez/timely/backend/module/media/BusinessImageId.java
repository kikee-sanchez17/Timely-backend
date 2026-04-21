package dev.esanchez.timely.backend.module.media;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class BusinessImageId implements Serializable {

    private Long businessId;
    private Long imageId;

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