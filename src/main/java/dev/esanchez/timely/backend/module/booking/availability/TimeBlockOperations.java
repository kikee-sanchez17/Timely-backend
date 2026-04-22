package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.shared.TimeBlock;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TimeBlockOperations {

    private TimeBlockOperations() {}

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

    public static List<TimeBlock> findOverlappingBlocks(
            List<TimeBlock> blockedTimeBlocks,
            OffsetDateTime startTimeSchedule,
            OffsetDateTime endTimeSchedule
    ) {
        return blockedTimeBlocks.stream()
                .filter(block ->
                        block.getStartTime().isBefore(endTimeSchedule) &&
                                block.getEndTime().isAfter(startTimeSchedule)
                )
                .sorted(Comparator.comparing(TimeBlock::getStartTime))
                .toList();
    }

    public static OffsetDateTime getEffectiveBlockStart(TimeBlock block, OffsetDateTime startTime) {
        return block.getStartTime().isBefore(startTime)
                ? startTime
                : block.getStartTime();
    }

    public static OffsetDateTime getEffectiveBlockEnd(TimeBlock block, OffsetDateTime endTime) {
        return block.getEndTime().isAfter(endTime)
                ? endTime
                : block.getEndTime();
    }

    public static List<TimeBlock> buildBlockedTimeBlocks(
            List<TimeBlock> bookingBlocks,
            List<TimeBlock> businessExceptionIntervals,
            List<TimeBlock> employeeExceptionIntervals
    ) {
        List<TimeBlock> blockedTimeBlocks = new ArrayList<>(bookingBlocks);
        blockedTimeBlocks.addAll(businessExceptionIntervals);
        blockedTimeBlocks.addAll(employeeExceptionIntervals);

        return mergeOverlappingBlocks(blockedTimeBlocks);
    }
}