package dev.esanchez.timely.backend.module.media;

import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.utilsCommon.ValidationUtils;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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

}