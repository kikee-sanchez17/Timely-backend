package dev.esanchez.timely.backend.controller;

import dev.esanchez.timely.backend.dto.request.CreateBookingRequest;
import dev.esanchez.timely.backend.dto.response.BookingResponse;
import dev.esanchez.timely.backend.entity.Booking;
import dev.esanchez.timely.backend.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponse createBooking(@RequestBody CreateBookingRequest request) {
        Booking booking = bookingService.createBooking(request);
        return new BookingResponse(request.getStartDatetime(),request.getSubserviceId(), request.getCustomerUserId(), request.getEmployeeId());
    }
}