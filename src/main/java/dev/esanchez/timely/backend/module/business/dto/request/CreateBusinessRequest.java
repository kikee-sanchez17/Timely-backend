package dev.esanchez.timely.backend.module.business.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateBusinessRequest {
    @NotBlank
    String name;
    @NotBlank
    Long categoryId;
    String info;
    @NotBlank
    String city;
    @NotBlank
    String countryCode;
    @NotBlank// "ES"
    String timezoneId;    // "Europe/Madrid"

}
