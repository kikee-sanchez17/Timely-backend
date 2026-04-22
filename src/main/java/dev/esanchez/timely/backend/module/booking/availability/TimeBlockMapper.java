package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.booking.Booking;
import dev.esanchez.timely.backend.module.shared.TimeBlock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static dev.esanchez.timely.backend.module.utilsCommon.ValidationUtils.toOffsetDateTime;

public final class TimeBlockMapper {

    private TimeBlockMapper() {}

    public static TimeBlock toTimeBlock(LocalDate date, LocalTime startTime, LocalTime endTime, ZoneId zoneId) {
        return new TimeBlock(
                toOffsetDateTime(date, startTime, zoneId),
                toOffsetDateTime(date, endTime, zoneId)
        );
    }

    public static List<TimeBlock> fromBookings(List<Booking> bookings) {
        List<TimeBlock> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(new TimeBlock(booking.getStartDatetime(), booking.getEndDatetime()));
        }

        return result;
    }
}