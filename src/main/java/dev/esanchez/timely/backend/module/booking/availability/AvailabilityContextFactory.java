package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.booking.dto.request.AvailableSlotRequest;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class AvailabilityContextFactory {

    public AvailabilityContext create(AvailabilityData data, AvailableSlotRequest request) {
        ZoneId zoneId = ZoneId.of(
                data.business()
                        .getCountryTimezone()
                        .getId()
                        .getTimezoneId()
        );

        return new AvailabilityContext(
                data.employee(),
                data.business(),
                data.subservice(),
                request.getDate(),
                zoneId,
                data.subservice().getDurationMinutes()
        );
    }
}
