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
public class SubserviceImageId implements Serializable {

    private Long subserviceId;
    private Long imageId;

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