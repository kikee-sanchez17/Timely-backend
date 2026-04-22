package dev.esanchez.timely.backend.module.booking;

import dev.esanchez.timely.backend.module.booking.availability.*;
import dev.esanchez.timely.backend.module.booking.creator.BookingCreator;
import dev.esanchez.timely.backend.module.booking.dto.request.AvailableSlotRequest;
import dev.esanchez.timely.backend.module.booking.dto.request.CreateBookingRequest;
import dev.esanchez.timely.backend.module.booking.dto.response.AvailableSlotDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingCreator bookingCreator;
    private final BookingLoader bookingLoader;
    private final AvailabilityContextFactory availabilityContextFactory;
    private final FullDayClosureChecker fullDayClosureChecker;
    private final AvailabilityBlockAssembler availabilityBlockAssembler;
    private final AvailableSlotCalculator availableSlotCalculator;

    public BookingServiceImpl(
            BookingCreator bookingCreator,
            BookingLoader bookingLoader,
            AvailabilityContextFactory availabilityContextFactory,
            FullDayClosureChecker fullDayClosureChecker,
            AvailabilityBlockAssembler availabilityBlockAssembler,
            AvailableSlotCalculator availableSlotCalculator
    ) {
        this.bookingCreator = bookingCreator;
        this.bookingLoader = bookingLoader;
        this.availabilityContextFactory = availabilityContextFactory;
        this.fullDayClosureChecker = fullDayClosureChecker;
        this.availabilityBlockAssembler = availabilityBlockAssembler;
        this.availableSlotCalculator = availableSlotCalculator;
    }

    @Override
    public Booking createBooking(CreateBookingRequest request) {
        return bookingCreator.create(request);
    }

    @Override
    public List<AvailableSlotDTO> calculateAvailableSlots(AvailableSlotRequest request) {
        AvailabilityData availabilityData = bookingLoader.loadAvailabilityData(request);

        AvailabilityContext context = availabilityContextFactory.create(availabilityData, request);

        if (fullDayClosureChecker.isClosed(context)) {
            return List.of();
        }

        AvailabilityBlocks availabilityBlocks = availabilityBlockAssembler.assemble(context);

        return availableSlotCalculator.calculate(availabilityBlocks, context);
    }
}