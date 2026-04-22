package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.services.Subservice;

import java.time.LocalDate;
import java.time.ZoneId;

public record AvailabilityContext(
        Employee employee,
        Business business,
        Subservice subservice,
        LocalDate date,
        ZoneId zoneId,
        Integer durationSubservice) {


    public int dayOfWeek() {
        return date.getDayOfWeek().getValue();
    }
}




