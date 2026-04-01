package dev.esanchez.timely.backend.dto.response;

import java.time.OffsetDateTime;

public class AvailableSlotDTO {

    private OffsetDateTime startDatetime;
    private OffsetDateTime endDatetime;
    private Integer duration;

    public AvailableSlotDTO(OffsetDateTime startDatetime, OffsetDateTime endDatetime, Integer duration) {
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.duration = duration;
    }

    public OffsetDateTime getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(OffsetDateTime startDatetime) {
        this.startDatetime = startDatetime;
    }

    public OffsetDateTime getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(OffsetDateTime endDatetime) {
        this.endDatetime = endDatetime;
    }

    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
