package dev.esanchez.timely.backend.service;

import dev.esanchez.timely.backend.dto.request.CreateBookingRequest;
import dev.esanchez.timely.backend.entity.Booking;
import dev.esanchez.timely.backend.entity.Employee;
import dev.esanchez.timely.backend.entity.Subservice;
import dev.esanchez.timely.backend.entity.User;
import dev.esanchez.timely.backend.repository.BookingRepository;
import dev.esanchez.timely.backend.repository.EmployeeRepository;
import dev.esanchez.timely.backend.repository.SubserviceRepository;
import dev.esanchez.timely.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final SubserviceRepository subserviceRepository;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            SubserviceRepository subserviceRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.subserviceRepository = subserviceRepository;
    }

    public Booking createBooking(CreateBookingRequest request) {
        // SELECT * FROM USERS where user_id = request.user_id
        User customer = userRepository.findById(request.getCustomerUserId())
                .orElseThrow(() -> new RuntimeException("Customer user not found"));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Subservice subservice = subserviceRepository.findById(request.getSubserviceId())
                .orElseThrow(() -> new RuntimeException("Subservice not found"));

        Booking booking = new Booking();
        booking.setStartDatetime(request.getStartDatetime());
        booking.setEndDatetime(request.getEndDatetime());
        booking.setSubservice(subservice);
        booking.setCustomerUser(customer);
        booking.setEmployee(employee);
        booking.setNotes(request.getNotes());

        return bookingRepository.save(booking);
    }
}