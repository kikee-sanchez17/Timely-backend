package dev.esanchez.timely.backend.controller;

import dev.esanchez.timely.backend.dto.request.CreateBookingRequest;
import dev.esanchez.timely.backend.entity.Booking;
import dev.esanchez.timely.backend.service.BookingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public String createBooking(@RequestBody CreateBookingRequest request) {
        Booking booking = bookingService.createBooking(request);
        return "Booking created with id: " + booking.getBookingId();
    }
}