package dev.esanchez.timely.backend.module.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateEmployeeRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String surname;

}
