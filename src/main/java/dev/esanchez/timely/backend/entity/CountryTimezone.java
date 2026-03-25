package dev.esanchez.timely.backend.entity;

import jakarta.persistence.*;

@Entity
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



    public CountryTimezone() {
    }

    public CountryTimezoneId getId() {
        return id;
    }

    public void setId(CountryTimezoneId id) {
        this.id = id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}