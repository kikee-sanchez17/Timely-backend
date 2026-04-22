package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.services.Subservice;

public record AvailabilityData(
        Employee employee,
        Business business,
        Subservice subservice
) {}
