package dev.esanchez.timely.backend.module.shared;

import java.time.OffsetDateTime;

public class TimeBlock {

    private final OffsetDateTime startTime;
    private final OffsetDateTime endTime;

    public TimeBlock(OffsetDateTime startTime, OffsetDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start and end time cannot be null");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }
    public OffsetDateTime getEndTime() {
        return endTime;
    }

}
