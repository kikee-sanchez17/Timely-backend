package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.booking.Booking;
import dev.esanchez.timely.backend.module.booking.BookingRepository;
import dev.esanchez.timely.backend.module.shared.TimeBlock;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class BookingBlockProvider {

    private final BookingRepository bookingRepository;

    public BookingBlockProvider(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<TimeBlock> getBlockedBookingBlocks(AvailabilityContext context) {
        OffsetDateTime startOfDay = context.date()
                .atStartOfDay(context.zoneId())
                .toOffsetDateTime();

        OffsetDateTime startOfNextDay = context.date()
                .plusDays(1)
                .atStartOfDay(context.zoneId())
                .toOffsetDateTime();

        List<Booking> bookings =
                bookingRepository.findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
                        context.employee().getEmployeeId(),
                        startOfDay,
                        startOfNextDay
                );

        return TimeBlockMapper.fromBookings(bookings);
    }
}
