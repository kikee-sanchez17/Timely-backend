package dev.esanchez.timely.backend.module.location;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "country_timezones")
public class CountryTimezone {

    @EmbeddedId
    private CountryTimezoneId id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) //Defines the relationship with the entity Country
    @JoinColumn(name = "country_code", nullable = false)//Indicates that the column country_code is the foreign key which connects with country
    @MapsId("countryCode") // countryCode from CountryTimezoneId is filled with the PK of the table Country.
    //Hibernate automatically detects that this foreign key references
    //the primary key of the Country table because it is annotated with @Id.
    private Country country;

}