package dev.esanchez.timely.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CountryTimezoneId implements Serializable {

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "timezone_id", nullable = false)
    private String timezoneId;

    public CountryTimezoneId() {
    }

    public CountryTimezoneId(String countryCode, String timezoneId) {
        this.countryCode = countryCode;
        this.timezoneId = timezoneId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getTimezoneId() {
        return timezoneId;
    }



    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CountryTimezoneId that)) return false;
        return Objects.equals(countryCode, that.countryCode) &&
                Objects.equals(timezoneId, that.timezoneId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, timezoneId);
    }
}