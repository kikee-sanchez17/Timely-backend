package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.shared.TimeBlock;

import java.util.List;

public record AvailabilityBlocks(
        List<TimeBlock> employeeScheduleBlocks,
        List<TimeBlock> employeeOpenExceptionBlocks,
        List<TimeBlock> businessScheduleBlocks,
        List<TimeBlock> businessOpenExceptionBlocks,
        List<TimeBlock> blockedTimeBlocks) {
}
