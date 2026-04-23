package dev.esanchez.timely.backend.module.booking;

import dev.esanchez.timely.backend.module.booking.dto.request.AvailableSlotRequest;
import dev.esanchez.timely.backend.module.booking.dto.request.CreateBookingRequest;
import dev.esanchez.timely.backend.module.booking.dto.response.AvailableSlotDTO;
import dev.esanchez.timely.backend.module.booking.dto.response.BookingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;


    @PostMapping
    public BookingResponse createBooking(@RequestBody CreateBookingRequest request) {
        Booking booking = bookingService.createBooking(request);
        return new BookingResponse(request.getStartDatetime(),request.getSubserviceId(), request.getCustomerUserId(), request.getEmployeeId());
    }

    @PostMapping("/availability")
    public ResponseEntity<List<AvailableSlotDTO>> getAvailability(@RequestBody AvailableSlotRequest request) {
        return ResponseEntity.ok(bookingService.calculateAvailableSlots(request));
    }


}