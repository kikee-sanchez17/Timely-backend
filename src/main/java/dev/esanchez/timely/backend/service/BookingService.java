package dev.esanchez.timely.backend.service;

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
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final SubserviceRepository subserviceRepository;
    private final BusinessScheduleRepository businessScheduleRepository;
    private final BusinessRepository businessRepository;
    private final EmployeeScheduleRepository employeeScheduleRepository;
    private final BusinessScheduleExceptionRepository businessScheduleExceptionRepository;
    private final EmployeeScheduleExceptionRepository employeeScheduleExceptionRepository;


    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            SubserviceRepository subserviceRepository,
            BusinessScheduleRepository businessScheduleRepository,
            BusinessRepository businessRepository,
            EmployeeScheduleRepository employeeScheduleRepository,
            BusinessScheduleExceptionRepository businessScheduleExceptionRepository,
            EmployeeScheduleExceptionRepository employeeScheduleExceptionRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.subserviceRepository = subserviceRepository;
        this.businessScheduleRepository = businessScheduleRepository;
        this.businessRepository = businessRepository;
        this.employeeScheduleRepository = employeeScheduleRepository;
        this.businessScheduleExceptionRepository = businessScheduleExceptionRepository;
        this.employeeScheduleExceptionRepository = employeeScheduleExceptionRepository;
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

        //Lo ideal es comprobar si esta activo.
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(request.getEmployeeId()));
        //Lo ideal es comprobar si esta activo
        Subservice subservice = subserviceRepository.findById(request.getSubserviceId())
                .orElseThrow(() -> new SubserviceNotFoundException(request.getSubserviceId()));

        Business business = employee.getBusiness();

        BusinessScheduleException businessScheduleException = businessScheduleExceptionRepository.findByIdAndDate(business.getBusinessId(),request.getDate());
        EmployeeScheduleException employeeScheduleException = employeeScheduleExceptionRepository.findByIdAndDate(employee.getEmployeeId(),request.getDate());


        List<AvailableSlotDTO> availableSlots = new ArrayList<>();


        if(businessScheduleException!=null){
            return List.of();
        }
        if(employeeScheduleException!=null){
            return List.of();
        }



        DayOfWeek dayOfWeek = request.getDate().getDayOfWeek();


        Integer durationSubservice = subservice.getDurationMinutes();

        //List with the employee schedule for the day that the user indicates
        List<EmployeeSchedule> employeeSchedules  =  employeeScheduleRepository.findAllByEmployee_employeeIdAndDayOfWeek(request.getEmployeeId(), dayOfWeek.getValue());

        //List with the business schedule for the day that the user indicates
        List<BusinessSchedule> businessSchedules  = businessScheduleRepository.findAllByBusiness_businessIdAndDayOfWeek(business.getBusinessId(), dayOfWeek.getValue());
        //Get timezone's business
        CountryTimezone countryTimezone= business.getCountryTimezone();
        CountryTimezoneId countryTimezoneId = countryTimezone.getId();
        ZoneId zoneId = ZoneId.of(countryTimezoneId.getTimezoneId());


        List <Booking> bookings = getBookingsForEmployeeOnDate(request.getDate(),employee.getEmployeeId(),zoneId);


        if (!employeeSchedules.isEmpty()) {

            for (EmployeeSchedule employeeSchedule : employeeSchedules) {

                OffsetDateTime startTimeEmployeeSchedule = toOffsetDateTime(request.getDate(), employeeSchedule.getStartTime(), zoneId);

                OffsetDateTime endTimeEmployeeSchedule = toOffsetDateTime(request.getDate(), employeeSchedule.getEndTime(), zoneId);

                processScheduleBlock(
                        availableSlots,
                        bookings,
                        startTimeEmployeeSchedule,
                        endTimeEmployeeSchedule,
                        durationSubservice
                );
            }

        }else{

            for (BusinessSchedule businessSchedule : businessSchedules) {

                OffsetDateTime startTimeBusinessSchedule = toOffsetDateTime(request.getDate(), businessSchedule.getStartTime(), zoneId);

                OffsetDateTime endTimeBusinessSchedule = toOffsetDateTime(request.getDate(), businessSchedule.getEndTime(), zoneId);

                processScheduleBlock(
                        availableSlots,
                        bookings,
                        startTimeBusinessSchedule,
                        endTimeBusinessSchedule,
                        durationSubservice
                );
            }
        }
        return availableSlots;

    }




    public void addSlotsBetweenTwoPoints(List<AvailableSlotDTO> availableSlots, long minutes, Integer durationSubservice, OffsetDateTime currentPoint){

        if(minutes >= durationSubservice){

            long slotsNumber = minutes / durationSubservice;
            OffsetDateTime startTime=currentPoint;

            for (int i = 0 ; i<slotsNumber;i++){
                availableSlots.add(new AvailableSlotDTO(startTime,startTime.plusMinutes(durationSubservice),durationSubservice));
                startTime = startTime.plusMinutes(durationSubservice);

            }

        }

    }

    public void addSlotsWhenThereIsNoBooking(List<AvailableSlotDTO> availableSlots, OffsetDateTime startTimeSchedule, OffsetDateTime endTimeSchedule, Integer durationSubservice){

        Duration duration = Duration.between(startTimeSchedule, endTimeSchedule);
        long minutes = duration.toMinutes();
        if (minutes >= durationSubservice) {
            long slotsNumber = minutes / durationSubservice;
            OffsetDateTime startTime=startTimeSchedule;
            for (int i = 0 ; i<slotsNumber;i++){
                availableSlots.add(new AvailableSlotDTO(startTime,startTime.plusMinutes(durationSubservice),durationSubservice));
                startTime = startTime.plusMinutes(durationSubservice);
            }
        }

    }

    public OffsetDateTime toOffsetDateTime(LocalDate date,LocalTime time , ZoneId zoneId){

        //It converts the StartTimeBusinessSchedule from Local Date to OffsetDateTime
        ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time, zoneId);
        return zonedDateTime.toOffsetDateTime();

    }

    public List <Booking> filterBookingsBySchedule (List <Booking> bookings, OffsetDateTime startTimeSchedule,  OffsetDateTime endTimeSchedule ){

        List<Booking> bookingsForThisSchedule;

        return bookingsForThisSchedule = bookings.stream()
                .filter(booking ->
                        booking.getStartDatetime().isBefore(endTimeSchedule) &&
                                booking.getEndDatetime().isAfter(startTimeSchedule)
                )
                .toList();

    }

    public OffsetDateTime getBookingEffectiveStart ( Booking booking, OffsetDateTime startTime ){

        return booking.getStartDatetime().isBefore(startTime)
                ? startTime
                : booking.getStartDatetime();
    }

    public OffsetDateTime getBookingEffectiveEnd ( Booking booking, OffsetDateTime startTime ){

        return booking.getEndDatetime().isAfter(startTime)
                ? startTime
                : booking.getEndDatetime();
    }

    public List <Booking> getBookingsForEmployeeOnDate( LocalDate date, Long employeeId, ZoneId zoneId){

        //EJ: 2019-09-01T00:00:00.00+02:00
        OffsetDateTime startOfDay = date
                .atStartOfDay(zoneId)
                .toOffsetDateTime();
        //EJ: 2019-09-02T00:00:00.00+02:00
        OffsetDateTime startOfNextDay = date
                .plusDays(1)
                .atStartOfDay(zoneId)
                .toOffsetDateTime();

        return bookingRepository.findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
                employeeId,
                startOfDay,
                startOfNextDay
        );

    }

    private void processScheduleBlock(List<AvailableSlotDTO> availableSlots, List<Booking> bookings, OffsetDateTime startTimeSchedule, OffsetDateTime endTimeSchedule, Integer durationSubservice){

        List<Booking> bookingsForThisSchedule = filterBookingsBySchedule(bookings, startTimeSchedule, endTimeSchedule);

        if(bookingsForThisSchedule.isEmpty()){

            addSlotsWhenThereIsNoBooking(availableSlots, startTimeSchedule, endTimeSchedule,durationSubservice);

        }else{

            OffsetDateTime currentPoint = startTimeSchedule;
            Duration duration;
            long minutes;

            for ( Booking booking : bookingsForThisSchedule ) {

                OffsetDateTime bookingEffectiveStart = getBookingEffectiveStart(booking,startTimeSchedule);

                OffsetDateTime bookingEffectiveEnd = getBookingEffectiveEnd(booking,endTimeSchedule);

                duration = Duration.between(currentPoint, bookingEffectiveStart);
                minutes = duration.toMinutes();

                addSlotsBetweenTwoPoints(availableSlots,minutes,durationSubservice,currentPoint);

                currentPoint = bookingEffectiveEnd;

            }

            duration = Duration.between(currentPoint, endTimeSchedule);
            minutes = duration.toMinutes();

            addSlotsBetweenTwoPoints(availableSlots,minutes,durationSubservice,currentPoint);

        }



    }


}