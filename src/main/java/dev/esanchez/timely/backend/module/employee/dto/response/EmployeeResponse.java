package dev.esanchez.timely.backend.module.employee.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

// EmployeeResponse.java
@Data
@Builder
public class EmployeeResponse {
    private Long employeeId;
    private String name;
    private String surname;
    private Boolean isActive;
    private OffsetDateTime createdAt;
}
