package dev.esanchez.timely.backend.module.booking.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AvailableSlotRequest {

    private Long customerUserId;
    private Long subserviceId;
    private Long employeeId;
    private LocalDate date;

}
