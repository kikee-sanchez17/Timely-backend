package dev.esanchez.timely.backend.module.booking.bookingUtils;

import dev.esanchez.timely.backend.module.booking.Booking;
import dev.esanchez.timely.backend.module.booking.dto.response.AvailableSlotDTO;
import dev.esanchez.timely.backend.module.shared.TimeBlock;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static dev.esanchez.timely.backend.module.utilsCommon.ValidationUtils.toOffsetDateTime;

public final class BookingUtils {

    private BookingUtils() {}

    public static TimeBlock toTimeBlock(LocalDate date, LocalTime startTime, LocalTime endTime, ZoneId zoneId) {
        return new TimeBlock(
                toOffsetDateTime(date, startTime, zoneId),
                toOffsetDateTime(date, endTime, zoneId)
        );
    }

    public static List<TimeBlock> mergeOverlappingBlocks(List<TimeBlock> blocks) {
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

    public static List <TimeBlock> findOverlappingBlocks (List <TimeBlock> blockedTimeBlocks, OffsetDateTime startTimeSchedule,  OffsetDateTime endTimeSchedule ){

        return  blockedTimeBlocks.stream()
                .filter(booking ->
                        booking.getStartTime().isBefore(endTimeSchedule) &&
                                booking.getEndTime().isAfter(startTimeSchedule)
                ).sorted(Comparator.comparing(TimeBlock::getStartTime))
                .toList();

    }

    public static OffsetDateTime getEffectiveBlockStart ( TimeBlock booking, OffsetDateTime startTime ){

        return booking.getStartTime().isBefore(startTime)
                ? startTime
                : booking.getStartTime();
    }

    public static OffsetDateTime getEffectiveBlockEnd ( TimeBlock booking, OffsetDateTime endTime ){

        return booking.getEndTime().isAfter(endTime)
                ? endTime
                : booking.getEndTime();
    }

    public static List<AvailableSlotDTO> generateSlotsInRange( OffsetDateTime rangeStart, OffsetDateTime rangeEnd, Integer subserviceDurationMinutes) {
        long minutes = Duration.between(rangeStart, rangeEnd).toMinutes();

        if (minutes < subserviceDurationMinutes) {
            return List.of();
        }

        List<AvailableSlotDTO> availableSlots = new ArrayList<>();

        long slotsNumber = minutes / subserviceDurationMinutes;
        OffsetDateTime current = rangeStart;

        for (int i = 0; i < slotsNumber; i++) {
            availableSlots.add(
                    new AvailableSlotDTO(
                            current,
                            current.plusMinutes(subserviceDurationMinutes),
                            subserviceDurationMinutes
                    )
            );
            current = current.plusMinutes(subserviceDurationMinutes);
        }
        return availableSlots;
    }

    public static List<AvailableSlotDTO> processAvailabilityBlock(
            List<TimeBlock> blockedTimeBlocks,
            TimeBlock availabilityBlock,
            Integer subserviceDurationMinutes
    ) {
        List<TimeBlock> blockedTimeBlocksForThisSchedule =
                BookingUtils.findOverlappingBlocks(blockedTimeBlocks, availabilityBlock.getStartTime(), availabilityBlock.getEndTime());

        if (blockedTimeBlocksForThisSchedule.isEmpty()) {
            return generateSlotsInRange(
                    availabilityBlock.getStartTime(),
                    availabilityBlock.getEndTime(),
                    subserviceDurationMinutes
            );

        }
        List<AvailableSlotDTO> availableSlots = new ArrayList<>();
        OffsetDateTime currentPoint = availabilityBlock.getStartTime();

        for (TimeBlock blockedTimeBlock  : blockedTimeBlocksForThisSchedule) {
            OffsetDateTime effectiveBlockStart = getEffectiveBlockStart(blockedTimeBlock , availabilityBlock.getStartTime());
            OffsetDateTime effectiveBlockEnd = getEffectiveBlockEnd(blockedTimeBlock , availabilityBlock.getEndTime());

            if (effectiveBlockStart.isAfter(currentPoint)) {
                availableSlots.addAll(generateSlotsInRange(
                        currentPoint,
                        effectiveBlockStart,
                        subserviceDurationMinutes
                ));
            }

            if (effectiveBlockEnd.isAfter(currentPoint)) {
                currentPoint = effectiveBlockEnd;
            }
        }

        return availableSlots;
    }

    public static List<TimeBlock> toTimeBlocksFromBookings(List<Booking> bookings) {
        List<TimeBlock> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(new TimeBlock(booking.getStartDatetime(), booking.getEndDatetime()));
        }
        return result;

    }

    public static List<TimeBlock> buildBlockedTimeBlocks(List <TimeBlock> bookingBlocks ,List <TimeBlock> businessExceptionInterval, List <TimeBlock> employeeExceptionInterval){
        List <TimeBlock> blockedTimeBlocks = new ArrayList<>(bookingBlocks);
        blockedTimeBlocks.addAll(businessExceptionInterval);
        blockedTimeBlocks.addAll(employeeExceptionInterval);
        return BookingUtils.mergeOverlappingBlocks(blockedTimeBlocks);
    }

}
