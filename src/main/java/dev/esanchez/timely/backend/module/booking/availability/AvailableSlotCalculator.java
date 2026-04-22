package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.booking.dto.response.AvailableSlotDTO;
import dev.esanchez.timely.backend.module.shared.TimeBlock;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class AvailableSlotCalculator {

    public List<AvailableSlotDTO> calculate(AvailabilityBlocks availabilityBlocks, AvailabilityContext context) {
        List<AvailableSlotDTO> availableSlots = new ArrayList<>();

        if (!availabilityBlocks.employeeScheduleBlocks().isEmpty()) {
            List<TimeBlock> blocks = new ArrayList<>(availabilityBlocks.employeeOpenExceptionBlocks());
            blocks.addAll(availabilityBlocks.employeeScheduleBlocks());
            blocks = TimeBlockOperations.mergeOverlappingBlocks(blocks);

            for (TimeBlock block : blocks) {
                availableSlots.addAll(
                        processAvailabilityBlock(
                                availabilityBlocks.blockedTimeBlocks(),
                                block,
                                context.durationSubservice()
                        )
                );
            }

            return availableSlots;
        }

        List<TimeBlock> blocks = new ArrayList<>(availabilityBlocks.businessOpenExceptionBlocks());
        blocks.addAll(availabilityBlocks.businessScheduleBlocks());
        blocks = TimeBlockOperations.mergeOverlappingBlocks(blocks);

        for (TimeBlock block : blocks) {
            availableSlots.addAll(
                    processAvailabilityBlock(
                            availabilityBlocks.blockedTimeBlocks(),
                            block,
                            context.durationSubservice()
                    )
            );
        }

        return availableSlots;
    }


    public static List<AvailableSlotDTO> generateSlotsInRange(OffsetDateTime rangeStart, OffsetDateTime rangeEnd, Integer subserviceDurationMinutes) {
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
                TimeBlockOperations.findOverlappingBlocks(blockedTimeBlocks, availabilityBlock.getStartTime(), availabilityBlock.getEndTime());

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
            OffsetDateTime effectiveBlockStart = TimeBlockOperations.getEffectiveBlockStart(blockedTimeBlock , availabilityBlock.getStartTime());
            OffsetDateTime effectiveBlockEnd = TimeBlockOperations.getEffectiveBlockEnd(blockedTimeBlock , availabilityBlock.getEndTime());

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
}


