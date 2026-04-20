package dev.esanchez.timely.backend.module.booking;

import dev.esanchez.timely.backend.module.booking.record.AvailabilityBlocks;
import dev.esanchez.timely.backend.module.booking.record.AvailabilityContext;
import dev.esanchez.timely.backend.module.shared.TimeBlock;
import dev.esanchez.timely.backend.module.booking.dto.request.AvailableSlotRequest;
import dev.esanchez.timely.backend.module.booking.dto.request.CreateBookingRequest;
import dev.esanchez.timely.backend.module.booking.dto.response.AvailableSlotDTO;
import dev.esanchez.timely.backend.module.location.CountryTimezone;
import dev.esanchez.timely.backend.module.location.CountryTimezoneId;
import dev.esanchez.timely.backend.module.shared.ExceptionIntervalType;
import dev.esanchez.timely.backend.module.business.exception.BusinessScheduleException;
import dev.esanchez.timely.backend.core.handleException.customHandleException.CustomerNotAuthenticatedException;
import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.identity.User;
import dev.esanchez.timely.backend.module.identity.UserRepository;
import dev.esanchez.timely.backend.module.business.BusinessSchedule;
import dev.esanchez.timely.backend.module.business.BusinessScheduleRepository;
import dev.esanchez.timely.backend.module.business.business.exception.BusinessExceptionInterval;
import dev.esanchez.timely.backend.module.business.exception.BusinessExceptionIntervalRepository;
import dev.esanchez.timely.backend.module.business.exception.BusinessScheduleExceptionRepository;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.employee.EmployeeRepository;
import dev.esanchez.timely.backend.module.employee.EmployeeSchedule;
import dev.esanchez.timely.backend.module.employee.EmployeeScheduleRepository;
import dev.esanchez.timely.backend.module.employee.exception.EmployeeExceptionInterval;
import dev.esanchez.timely.backend.module.employee.exception.EmployeeExceptionIntervalRepository;
import dev.esanchez.timely.backend.module.employee.exception.EmployeeScheduleException;
import dev.esanchez.timely.backend.module.employee.exception.EmployeeScheduleExceptionRepository;
import dev.esanchez.timely.backend.module.services.Subservice;
import dev.esanchez.timely.backend.module.services.SubserviceRepository;
import dev.esanchez.timely.backend.module.booking.bookingUtils.BookingUtils;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final SubserviceRepository subserviceRepository;
    private final BusinessScheduleRepository businessScheduleRepository;
    private final EmployeeScheduleRepository employeeScheduleRepository;
    private final BusinessScheduleExceptionRepository businessScheduleExceptionRepository;
    private final EmployeeScheduleExceptionRepository employeeScheduleExceptionRepository;
    private final BusinessExceptionIntervalRepository businessExceptionIntervalRepository;
    private final EmployeeExceptionIntervalRepository employeeExceptionIntervalRepository;


    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            SubserviceRepository subserviceRepository,
            BusinessScheduleRepository businessScheduleRepository,
            EmployeeScheduleRepository employeeScheduleRepository,
            BusinessScheduleExceptionRepository businessScheduleExceptionRepository,
            EmployeeScheduleExceptionRepository employeeScheduleExceptionRepository,
            BusinessExceptionIntervalRepository businessExceptionIntervalRepository,
            EmployeeExceptionIntervalRepository employeeExceptionIntervalRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.subserviceRepository = subserviceRepository;
        this.businessScheduleRepository = businessScheduleRepository;
        this.employeeScheduleRepository = employeeScheduleRepository;
        this.businessScheduleExceptionRepository = businessScheduleExceptionRepository;
        this.employeeScheduleExceptionRepository = employeeScheduleExceptionRepository;
        this.businessExceptionIntervalRepository = businessExceptionIntervalRepository;
        this.employeeExceptionIntervalRepository = employeeExceptionIntervalRepository;
    }

    public Booking createBooking(CreateBookingRequest request) {

        User customer = userRepository.findById(request.getCustomerUserId())
                .orElseThrow(CustomerNotAuthenticatedException::new);

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee not found with ID: ",request.getEmployeeId()));

        Subservice subservice = subserviceRepository.findById(request.getSubserviceId())
                .orElseThrow(() -> new NotFoundException("Subservice not found with ID: ",request.getSubserviceId()));

        Booking booking = new Booking();
        booking.setStartDatetime(request.getStartDatetime());
        booking.setEndDatetime(request.getEndDatetime());
        booking.setSubservice(subservice);
        booking.setCustomerUser(customer);
        booking.setEmployee(employee);
        booking.setNotes(request.getNotes());

        return bookingRepository.save(booking);
    }

    public List<AvailableSlotDTO> calculateAvailableSlots(AvailableSlotRequest request) {

        Employee employee = getEmployeeOrThrow(request.getEmployeeId());

        Subservice subservice = getSubserviceOrThrow(request.getSubserviceId());

        Business business = employee.getBusiness();

        ZoneId zoneId = getZoneId(business);

        AvailabilityContext context = new AvailabilityContext(
                employee,
                business,
                subservice,
                request.getDate(),
                zoneId,
                subservice.getDurationMinutes()
        );

        if (isClosedForFullDay(context)) return List.of();

        List <EmployeeExceptionInterval> employeeExceptionIntervals = employeeExceptionIntervalRepository.findByEmployee_EmployeeIdAndDate(employee.getEmployeeId(),request.getDate());
        List <BusinessExceptionInterval> businessExceptionIntervals = businessExceptionIntervalRepository.findByBusiness_BusinessIdAndDate(business.getBusinessId(),request.getDate());

        List <TimeBlock> businessOpenExceptionBlocks = filterByBusinessExceptionIntervalSchedules(businessExceptionIntervals, ExceptionIntervalType.OPEN_INTERVAL,request.getDate(),zoneId);
        List <TimeBlock> businessClosedExceptionBlocks = filterByBusinessExceptionIntervalSchedules(businessExceptionIntervals,ExceptionIntervalType.CLOSED_INTERVAL,request.getDate(),zoneId);

        List <TimeBlock> employeeOpenExceptionBlocks = filterByEmployeeExceptionIntervalSchedules(employeeExceptionIntervals,ExceptionIntervalType.OPEN_INTERVAL,request.getDate(),zoneId);
        List <TimeBlock> employeeClosedExceptionBlocks = filterByEmployeeExceptionIntervalSchedules(employeeExceptionIntervals,ExceptionIntervalType.CLOSED_INTERVAL,request.getDate(),zoneId);


        List <TimeBlock> businessScheduleBlocks = toAvailabilityBlocksFromBusinessSchedules(business,context.dayOfWeek(),zoneId,request.getDate());

        List <TimeBlock> employeeScheduleBlocks = toAvailabilityBlocksFromEmployeeSchedules(employee,context.dayOfWeek(),zoneId,request.getDate());

        List <Booking> bookings = getBookingsForEmployeeOnDate(context);

        List <TimeBlock> bookingBlocks = BookingUtils.toTimeBlocksFromBookings(bookings);

        List<TimeBlock> blockedTimeBlocks = BookingUtils.buildBlockedTimeBlocks(
                bookingBlocks,
                businessClosedExceptionBlocks,
                employeeClosedExceptionBlocks);

        AvailabilityBlocks availabilityBlocks = new AvailabilityBlocks(
                employeeScheduleBlocks,
                employeeOpenExceptionBlocks,
                businessScheduleBlocks,
                businessOpenExceptionBlocks,
                blockedTimeBlocks);

        return buildAvailableSlots(availabilityBlocks,context);
    }

    private List <Booking> getBookingsForEmployeeOnDate( AvailabilityContext context){

        OffsetDateTime startOfDay = context.date()
                .atStartOfDay(context.zoneId())
                .toOffsetDateTime();

        OffsetDateTime startOfNextDay = context.date()
                .plusDays(1)
                .atStartOfDay(context.zoneId())
                .toOffsetDateTime();
        List<Booking> bookings = bookingRepository.findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
                context.employee().getEmployeeId(),
                startOfDay,
                startOfNextDay
        );
        return bookings;

    }

    //Filter the business intervals exception if these are open or closed
    private List<TimeBlock> filterByBusinessExceptionIntervalSchedules(
            List<BusinessExceptionInterval> businessExceptionIntervals,
            ExceptionIntervalType exceptionIntervalType, LocalDate date, ZoneId zoneId) {

        List<TimeBlock> result = new ArrayList<>();

        for (BusinessExceptionInterval businessExceptionInterval : businessExceptionIntervals) {
            if (businessExceptionInterval.getIntervalType() == exceptionIntervalType) {
                result.add(BookingUtils.toTimeBlock(date,businessExceptionInterval.getStartTime(),businessExceptionInterval.getEndTime(),zoneId));
            }
        }

        return result;
    }
    //Filter the employee intervals exception if these are open or closed
    private List<TimeBlock> filterByEmployeeExceptionIntervalSchedules(
            List<EmployeeExceptionInterval> employeeExceptionIntervals,
            ExceptionIntervalType exceptionIntervalType, LocalDate date, ZoneId zoneId) {

        List<TimeBlock> result = new ArrayList<>();

        for (EmployeeExceptionInterval employeeExceptionInterval : employeeExceptionIntervals) {
            if (employeeExceptionInterval.getIntervalType() == exceptionIntervalType) {
                result.add(BookingUtils.toTimeBlock(date,employeeExceptionInterval.getStartTime(),employeeExceptionInterval.getEndTime(),zoneId));
            }
        }

        return result;
    }


    private List <TimeBlock> toAvailabilityBlocksFromBusinessSchedules(Business business, int dayOfWeek , ZoneId zoneId,LocalDate date) {
        //List with the business schedule for the day that the user indicates
        List <BusinessSchedule> businessSchedules  = businessScheduleRepository.findAllByBusiness_businessIdAndDayOfWeek(business.getBusinessId(), dayOfWeek);

        List<TimeBlock> result = new ArrayList<>();

        for (BusinessSchedule businessSchedule : businessSchedules) {
            result.add(BookingUtils.toTimeBlock(date,businessSchedule.getStartTime(),businessSchedule.getEndTime(),zoneId));
        }
        return result;
    }

    private List <TimeBlock> toAvailabilityBlocksFromEmployeeSchedules(Employee employee, int dayOfWeek , ZoneId zoneId,LocalDate date) {
        //List with the employee schedule for the day that the user indicates
        List <EmployeeSchedule> employeeSchedules  =  employeeScheduleRepository.findAllByEmployee_employeeIdAndDayOfWeek(employee.getEmployeeId(), dayOfWeek);

        List<TimeBlock> result = new ArrayList<>();

        for (EmployeeSchedule employeeSchedule : employeeSchedules) {
            result.add(BookingUtils.toTimeBlock(date,employeeSchedule.getStartTime(),employeeSchedule.getEndTime(),zoneId));
        }
        return result;
    }

    private ZoneId getZoneId(Business business) {
        //Get timezone's business
        CountryTimezone countryTimezone= business.getCountryTimezone();
        CountryTimezoneId countryTimezoneId = countryTimezone.getId();
        return ZoneId.of(countryTimezoneId.getTimezoneId());

    }

    private boolean isClosedForFullDay(AvailabilityContext context) {

        Optional <BusinessScheduleException> businessScheduleException = businessScheduleExceptionRepository.findByBusiness_BusinessIdAndDate(context.business().getBusinessId(),context.date());
        Optional <EmployeeScheduleException> employeeScheduleException = employeeScheduleExceptionRepository.findByEmployee_EmployeeIdAndDate(context.employee().getEmployeeId(),context.date());


        return businessScheduleException.isPresent()  || employeeScheduleException.isPresent();

    }


    private List <AvailableSlotDTO> buildAvailableSlots(AvailabilityBlocks availabilityBlocks, AvailabilityContext context) {
        List<AvailableSlotDTO> availableSlots = new ArrayList<>();
        //checks if employee has their own schedule
        if (!availabilityBlocks.employeeScheduleBlocks().isEmpty()) {
            //Merge employee Schedule with intervals open
            List<TimeBlock> employeeScheduleWithIntervalOpen = new ArrayList<>(availabilityBlocks.employeeOpenExceptionBlocks());
            employeeScheduleWithIntervalOpen.addAll(availabilityBlocks.employeeScheduleBlocks());
            employeeScheduleWithIntervalOpen = BookingUtils.mergeOverlappingBlocks(employeeScheduleWithIntervalOpen);

            for (TimeBlock employeeSchedule : employeeScheduleWithIntervalOpen) {

              return BookingUtils.processAvailabilityBlock(
                      availabilityBlocks.blockedTimeBlocks(),
                        employeeSchedule,
                        context.durationSubservice()
                );
            }
        }else{

            List<TimeBlock> businessScheduleWithIntervalOpen = new ArrayList<>(availabilityBlocks.businessOpenExceptionBlocks());
            businessScheduleWithIntervalOpen.addAll(availabilityBlocks.businessScheduleBlocks());
            businessScheduleWithIntervalOpen = BookingUtils.mergeOverlappingBlocks(businessScheduleWithIntervalOpen);

            for (TimeBlock businessSchedule : businessScheduleWithIntervalOpen) {

                availableSlots.addAll(BookingUtils.processAvailabilityBlock(
                        availabilityBlocks.blockedTimeBlocks(),
                        businessSchedule,
                        context.durationSubservice()
                ));
            }
        }
        return availableSlots;
    }

    private Employee getEmployeeOrThrow(Long employeeId){

        return employeeRepository.findById(employeeId).orElseThrow(() -> new NotFoundException("Employee not found with ID: ", employeeId));

    }

    private Subservice getSubserviceOrThrow(Long subserviceId){

        return subserviceRepository.findById(subserviceId).orElseThrow(() -> new NotFoundException("Subservice not found with ID: ", subserviceId));

    }


}