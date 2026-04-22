package dev.esanchez.timely.backend.module.booking;

import dev.esanchez.timely.backend.module.booking.dto.request.AvailableSlotRequest;
import dev.esanchez.timely.backend.module.booking.dto.request.CreateBookingRequest;
import dev.esanchez.timely.backend.module.booking.dto.response.AvailableSlotDTO;
import dev.esanchez.timely.backend.module.booking.dto.response.BookingResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingServiceImpl bookingService;

    public BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponse createBooking(@RequestBody CreateBookingRequest request) {
        Booking booking = bookingService.createBooking(request);
        return new BookingResponse(request.getStartDatetime(),request.getSubserviceId(), request.getCustomerUserId(), request.getEmployeeId());
    }

    @PostMapping("/availability")
    public List<AvailableSlotDTO> getAvailability(@RequestBody AvailableSlotRequest request) {
        return bookingService.calculateAvailableSlots(request);
    }


}