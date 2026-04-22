package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.module.booking.dto.request.AvailableSlotRequest;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.employee.EmployeeRepository;
import dev.esanchez.timely.backend.module.services.Subservice;
import dev.esanchez.timely.backend.module.services.SubserviceRepository;
import org.springframework.stereotype.Component;

@Component
public class BookingLoader {

    private final EmployeeRepository employeeRepository;
    private final SubserviceRepository subserviceRepository;

    public BookingLoader(EmployeeRepository employeeRepository,
                         SubserviceRepository subserviceRepository) {
        this.employeeRepository = employeeRepository;
        this.subserviceRepository = subserviceRepository;
    }

    public AvailabilityData loadAvailabilityData(AvailableSlotRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee not found with ID: ", request.getEmployeeId()));

        Subservice subservice = subserviceRepository.findById(request.getSubserviceId())
                .orElseThrow(() -> new NotFoundException("Subservice not found with ID: ", request.getSubserviceId()));

        Business business = employee.getBusiness();

        return new AvailabilityData(employee, business, subservice);
    }
}
