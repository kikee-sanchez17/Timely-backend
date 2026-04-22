package dev.esanchez.timely.backend.module.booking;

import dev.esanchez.timely.backend.module.booking.dto.request.AvailableSlotRequest;
import dev.esanchez.timely.backend.module.booking.dto.request.CreateBookingRequest;
import dev.esanchez.timely.backend.module.booking.dto.response.AvailableSlotDTO;

import java.util.List;

public interface BookingService {

    Booking createBooking(CreateBookingRequest request);

    List<AvailableSlotDTO> calculateAvailableSlots(AvailableSlotRequest request);

}
