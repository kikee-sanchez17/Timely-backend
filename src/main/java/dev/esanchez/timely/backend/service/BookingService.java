package dev.esanchez.timely.backend.service;

import dev.esanchez.timely.backend.domain.TimeBlock;
import dev.esanchez.timely.backend.dto.request.AvailableSlotRequest;
import dev.esanchez.timely.backend.dto.request.CreateBookingRequest;
import dev.esanchez.timely.backend.dto.response.AvailableSlotDTO;
import dev.esanchez.timely.backend.entity.*;
import dev.esanchez.timely.backend.entity.BusinessScheduleException;
import dev.esanchez.timely.backend.exception.CustomerNotAuthenticatedException;
import dev.esanchez.timely.backend.exception.EmployeeNotFoundException;
import dev.esanchez.timely.backend.exception.SubserviceNotFoundException;
import dev.esanchez.timely.backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
                .orElseThrow(() -> new EmployeeNotFoundException(request.getEmployeeId()));

        Subservice subservice = subserviceRepository.findById(request.getSubserviceId())
                .orElseThrow(() -> new SubserviceNotFoundException(request.getSubserviceId()));

        Booking booking = new Booking();
        booking.setStartDatetime(request.getStartDatetime());
        booking.setEndDatetime(request.getEndDatetime());
        booking.setSubservice(subservice);
        booking.setCustomerUser(customer);
        booking.setEmployee(employee);
        booking.setNotes(request.getNotes());

        return bookingRepository.save(booking);
    }

    public List<AvailableSlotDTO> getAvailableSlots(AvailableSlotRequest request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(request.getEmployeeId()));

        Subservice subservice = subserviceRepository.findById(request.getSubserviceId())
                .orElseThrow(() -> new SubserviceNotFoundException(request.getSubserviceId()));

        Business business = employee.getBusiness();

        BusinessScheduleException businessScheduleException = businessScheduleExceptionRepository.findByBusiness_BusinessIdAndDate(business.getBusinessId(),request.getDate());
        EmployeeScheduleException employeeScheduleException = employeeScheduleExceptionRepository.findByEmployee_EmployeeIdAndDate(employee.getEmployeeId(),request.getDate());

        if (isClosedForFullDay(businessScheduleException,employeeScheduleException )) return List.of();

        DayOfWeek dayOfWeek = request.getDate().getDayOfWeek();

        Integer durationSubservice = subservice.getDurationMinutes();

        ZoneId zoneId = getZoneId(business);

        List <BusinessExceptionInterval> businessExceptionIntervals = businessExceptionIntervalRepository.findByBusiness_BusinessIdAndDate(business.getBusinessId(),request.getDate());

        List <EmployeeExceptionInterval> employeeExceptionIntervals = employeeExceptionIntervalRepository.findByEmployee_EmployeeIdAndDate(employee.getEmployeeId(),request.getDate());

        List <TimeBlock> businessExceptionIntervalsOpen = filterByBusinessExceptionIntervalSchedules(businessExceptionIntervals,ExceptionIntervalType.OPEN_INTERVAL,request.getDate(),zoneId);
        List <TimeBlock> businessExceptionIntervalsClosed = filterByBusinessExceptionIntervalSchedules(businessExceptionIntervals,ExceptionIntervalType.CLOSED_INTERVAL,request.getDate(),zoneId);

        List <TimeBlock> employeeExceptionIntervalsOpen = filterByEmployeeExceptionIntervalSchedules(employeeExceptionIntervals,ExceptionIntervalType.OPEN_INTERVAL,request.getDate(),zoneId);
        List <TimeBlock> employeeExceptionIntervalsClosed = filterByEmployeeExceptionIntervalSchedules(employeeExceptionIntervals,ExceptionIntervalType.CLOSED_INTERVAL,request.getDate(),zoneId);

        //List with the employee schedule for the day that the user indicates
        List <EmployeeSchedule> employeeSchedules  =  employeeScheduleRepository.findAllByEmployee_employeeIdAndDayOfWeek(request.getEmployeeId(), dayOfWeek.getValue());

        //List with the business schedule for the day that the user indicates
        List <BusinessSchedule> businessSchedules  = businessScheduleRepository.findAllByBusiness_businessIdAndDayOfWeek(business.getBusinessId(), dayOfWeek.getValue());

        List <TimeBlock> timeBlocksBusinessSchedules = toAvailabilityBlocksFromBusinessSchedules(businessSchedules,zoneId,request.getDate());

        List <TimeBlock> timeBlocksEmployeeSchedules = toAvailabilityBlocksFromEmployeeSchedules(employeeSchedules,zoneId,request.getDate());

        List <Booking> bookings = getBookingsForEmployeeOnDate(request.getDate(),employee.getEmployeeId(),zoneId);

        List <TimeBlock> timeBlocksBookings = toTimeBlocksFromBookings(bookings);


        List<TimeBlock> blockedTimeBlocks = new ArrayList<>(timeBlocksBookings);
        blockedTimeBlocks.addAll(businessExceptionIntervalsClosed);
        blockedTimeBlocks.addAll(employeeExceptionIntervalsClosed);
        blockedTimeBlocks = mergeOverlappingTimeBlocks(blockedTimeBlocks);

        return getAvailableSlots(timeBlocksEmployeeSchedules,employeeExceptionIntervalsOpen,blockedTimeBlocks,durationSubservice,businessExceptionIntervalsOpen
        ,timeBlocksBusinessSchedules);
    }


    private void addSlotsInRange(List<AvailableSlotDTO> availableSlots,OffsetDateTime rangeStart, OffsetDateTime rangeEnd, Integer durationSubservice) {
        long minutes = Duration.between(rangeStart, rangeEnd).toMinutes();

        if (minutes < durationSubservice) {
            return;
        }

        long slotsNumber = minutes / durationSubservice;
        OffsetDateTime current = rangeStart;

        for (int i = 0; i < slotsNumber; i++) {
            availableSlots.add(
                    new AvailableSlotDTO(
                            current,
                            current.plusMinutes(durationSubservice),
                            durationSubservice
                    )
            );
            current = current.plusMinutes(durationSubservice);
        }
    }
    //It converts
    private OffsetDateTime toOffsetDateTime(LocalDate date,LocalTime time , ZoneId zoneId){

        ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time, zoneId);
        return zonedDateTime.toOffsetDateTime();
    }

    private List <TimeBlock> filterBlocksByOverlap (List <TimeBlock> blockedTimeBlocks, OffsetDateTime startTimeSchedule,  OffsetDateTime endTimeSchedule ){

        return  blockedTimeBlocks.stream()
                .filter(booking ->
                        booking.getStartTime().isBefore(endTimeSchedule) &&
                                booking.getEndTime().isAfter(startTimeSchedule)
                ).sorted(Comparator.comparing(TimeBlock::getStartTime))
                .toList();

    }

    private OffsetDateTime getEffectiveBlockStart ( TimeBlock booking, OffsetDateTime startTime ){

        return booking.getStartTime().isBefore(startTime)
                ? startTime
                : booking.getStartTime();
    }

    private OffsetDateTime getEffectiveBlockEnd ( TimeBlock booking, OffsetDateTime endTime ){

        return booking.getEndTime().isAfter(endTime)
                ? endTime
                : booking.getEndTime();
    }

    private List <Booking> getBookingsForEmployeeOnDate( LocalDate date, Long employeeId, ZoneId zoneId){

        //EJ: 2019-09-01T00:00:00.00+02:00
        OffsetDateTime startOfDay = date
                .atStartOfDay(zoneId)
                .toOffsetDateTime();
        //EJ: 2019-09-02T00:00:00.00+02:00
        OffsetDateTime startOfNextDay = date
                .plusDays(1)
                .atStartOfDay(zoneId)
                .toOffsetDateTime();
        List<Booking> bookings = bookingRepository.findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
                employeeId,
                startOfDay,
                startOfNextDay
        );
        return bookings;

    }

    private void processAvailabilityBlock(
            List<AvailableSlotDTO> availableSlots,
            List<TimeBlock> blockedTimeBlocks,
            TimeBlock availabilityBlock,
            Integer durationSubservice
    ) {
        List<TimeBlock> blockedTimeBlocksForThisSchedule =
                filterBlocksByOverlap(blockedTimeBlocks, availabilityBlock.getStartTime(), availabilityBlock.getEndTime());

        if (blockedTimeBlocksForThisSchedule.isEmpty()) {
            addSlotsInRange(
                    availableSlots,
                    availabilityBlock.getStartTime(),
                    availabilityBlock.getEndTime(),
                    durationSubservice
            );
            return;
        }

        OffsetDateTime currentPoint = availabilityBlock.getStartTime();

        for (TimeBlock blockedTimeBlock  : blockedTimeBlocksForThisSchedule) {
            OffsetDateTime effectiveBlockStart = getEffectiveBlockStart(blockedTimeBlock , availabilityBlock.getStartTime());
            OffsetDateTime effectiveBlockEnd = getEffectiveBlockEnd(blockedTimeBlock , availabilityBlock.getEndTime());

            if (effectiveBlockStart.isAfter(currentPoint)) {
                addSlotsInRange(
                        availableSlots,
                        currentPoint,
                        effectiveBlockStart,
                        durationSubservice
                );
            }

            if (effectiveBlockEnd.isAfter(currentPoint)) {
                currentPoint = effectiveBlockEnd;
            }
        }

        addSlotsInRange(
                availableSlots,
                currentPoint,
                availabilityBlock.getEndTime(),
                durationSubservice
        );
    }

    //Filter the business intervals exception if these are open or closed
    private List<TimeBlock> filterByBusinessExceptionIntervalSchedules(
            List<BusinessExceptionInterval> businessExceptionIntervals,
            ExceptionIntervalType exceptionIntervalType, LocalDate date, ZoneId zoneId) {

        List<TimeBlock> result = new ArrayList<>();

        for (BusinessExceptionInterval businessExceptionInterval : businessExceptionIntervals) {
            if (businessExceptionInterval.getIntervalType() == exceptionIntervalType) {
                result.add(toTimeBlock(date,businessExceptionInterval.getStartTime(),businessExceptionInterval.getEndTime(),zoneId));
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
                result.add(toTimeBlock(date,employeeExceptionInterval.getStartTime(),employeeExceptionInterval.getEndTime(),zoneId));
            }
        }

        return result;
    }

    private List<TimeBlock> mergeOverlappingTimeBlocks(List<TimeBlock> blocks) {
        if (blocks.isEmpty()) {
            return List.of();
        }

        List<TimeBlock> sortedBlocks = new ArrayList<>(blocks);
        sortedBlocks.sort(Comparator.comparing(TimeBlock::getStartTime));

        List<TimeBlock> merged = new ArrayList<>();
        TimeBlock current = sortedBlocks.get(0);

        for (int i = 1; i < sortedBlocks.size(); i++) {
            TimeBlock next = sortedBlocks.get(i);

            if (!next.getStartTime().isAfter(current.getEndTime())) {
                OffsetDateTime mergedEnd = current.getEndTime().isAfter(next.getEndTime())
                        ? current.getEndTime()
                        : next.getEndTime();

                current = new TimeBlock(current.getStartTime(), mergedEnd);
            } else {
                merged.add(current);
                current = next;
            }
        }

        merged.add(current);

        return merged;
    }

    private List<TimeBlock> toTimeBlocksFromBookings(List<Booking> bookings) {
        List<TimeBlock> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(new TimeBlock(booking.getStartDatetime(), booking.getEndDatetime()));
        }
        return result;

    }

    private List <TimeBlock> toAvailabilityBlocksFromBusinessSchedules(List<BusinessSchedule> businessSchedules, ZoneId zoneId,LocalDate date) {
        List<TimeBlock> result = new ArrayList<>();

        for (BusinessSchedule businessSchedule : businessSchedules) {
            result.add(toTimeBlock(date,businessSchedule.getStartTime(),businessSchedule.getEndTime(),zoneId));
        }
        return result;
    }

    private List <TimeBlock> toAvailabilityBlocksFromEmployeeSchedules(List<EmployeeSchedule> employeeSchedules, ZoneId zoneId,LocalDate date) {
        List<TimeBlock> result = new ArrayList<>();

        for (EmployeeSchedule employeeSchedule : employeeSchedules) {
            result.add(toTimeBlock(date,employeeSchedule.getStartTime(),employeeSchedule.getEndTime(),zoneId));
        }
        return result;
    }

    private ZoneId getZoneId(Business business) {
        //Get timezone's business
        CountryTimezone countryTimezone= business.getCountryTimezone();
        CountryTimezoneId countryTimezoneId = countryTimezone.getId();
        return ZoneId.of(countryTimezoneId.getTimezoneId());


    }

    private boolean isClosedForFullDay(BusinessScheduleException businessScheduleException , EmployeeScheduleException employeeScheduleException) {

        return businessScheduleException != null || employeeScheduleException != null;

    }
    private TimeBlock toTimeBlock(LocalDate date, LocalTime startTime, LocalTime endTime, ZoneId zoneId) {
        return new TimeBlock(
                toOffsetDateTime(date, startTime, zoneId),
                toOffsetDateTime(date, endTime, zoneId)
        );
    }

    private List <AvailableSlotDTO> getAvailableSlots(List <TimeBlock> timeBlocksEmployeeSchedules, List <TimeBlock> employeeExceptionIntervalsOpen, List <TimeBlock> blockedTimeBlocks, Integer durationSubservice, List <TimeBlock> businessExceptionIntervalsOpen, List <TimeBlock> timeBlocksBusinessSchedules) {
        List<AvailableSlotDTO> availableSlots = new ArrayList<>();
        //checks if employee has their own schedule
        if (!timeBlocksEmployeeSchedules.isEmpty()) {
            //Merge employee Schedule with intervals open
            List<TimeBlock> employeeScheduleWithIntervalOpen = new ArrayList<>(employeeExceptionIntervalsOpen);
            employeeScheduleWithIntervalOpen.addAll(timeBlocksEmployeeSchedules);
            employeeScheduleWithIntervalOpen = mergeOverlappingTimeBlocks(employeeScheduleWithIntervalOpen);

            for (TimeBlock employeeSchedule : employeeScheduleWithIntervalOpen) {

                processAvailabilityBlock(
                        availableSlots,
                        blockedTimeBlocks,
                        employeeSchedule,
                        durationSubservice
                );
            }
        }else{

            List<TimeBlock> businessScheduleWithIntervalOpen = new ArrayList<>(businessExceptionIntervalsOpen);
            businessScheduleWithIntervalOpen.addAll(timeBlocksBusinessSchedules);
            businessScheduleWithIntervalOpen = mergeOverlappingTimeBlocks(businessScheduleWithIntervalOpen);

            for (TimeBlock businessSchedule : businessScheduleWithIntervalOpen) {

                processAvailabilityBlock(
                        availableSlots,
                        blockedTimeBlocks,
                        businessSchedule,
                        durationSubservice
                );
            }
        }
        return availableSlots;
    }


}