package dev.esanchez.timely.backend.module.location;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "countries")
public class Country {

    @Id
    @Column(name = "code", nullable = false ,length = 2)
    private String code;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "flag_emoji")
    private String flagEmoji;

}
