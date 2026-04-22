package dev.esanchez.timely.backend.module.booking.creator;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.core.handleException.customHandleException.CustomerNotAuthenticatedException;
import dev.esanchez.timely.backend.module.booking.Booking;
import dev.esanchez.timely.backend.module.booking.BookingRepository;
import dev.esanchez.timely.backend.module.booking.dto.request.CreateBookingRequest;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.employee.EmployeeRepository;
import dev.esanchez.timely.backend.module.identity.User;
import dev.esanchez.timely.backend.module.identity.UserRepository;
import dev.esanchez.timely.backend.module.services.Subservice;
import dev.esanchez.timely.backend.module.services.SubserviceRepository;
import org.springframework.stereotype.Component;

@Component
public class BookingCreator {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final SubserviceRepository subserviceRepository;

    public BookingCreator(
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

    public Booking create(CreateBookingRequest request) {
        User customer = userRepository.findById(request.getCustomerUserId())
                .orElseThrow(CustomerNotAuthenticatedException::new);

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee not found with ID: ", request.getEmployeeId()));

        Subservice subservice = subserviceRepository.findById(request.getSubserviceId())
                .orElseThrow(() -> new NotFoundException("Subservice not found with ID: ", request.getSubserviceId()));

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
