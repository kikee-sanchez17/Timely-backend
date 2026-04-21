package dev.esanchez.timely.backend.module.shared;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
public class TimeBlock {

    private final OffsetDateTime startTime;
    private final OffsetDateTime endTime;

    public TimeBlock(OffsetDateTime startTime, OffsetDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
