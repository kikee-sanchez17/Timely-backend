package dev.esanchez.timely.backend.module.media;

import dev.esanchez.timely.backend.module.services.Subservice;
import dev.esanchez.timely.backend.module.utilsCommon.ValidationUtils;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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

}