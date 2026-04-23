package dev.esanchez.timely.backend.module.business.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateBusinessRequest {

    String name;
    Long categoryId;
    String info;
    String city;
    String countryCode;   // "ES"
    String timezoneId;    // "Europe/Madrid"

}
