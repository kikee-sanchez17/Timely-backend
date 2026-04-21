package dev.esanchez.timely.backend.module.media;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServiceImageId implements Serializable {

    private Long serviceId;
    private Long imageId;

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