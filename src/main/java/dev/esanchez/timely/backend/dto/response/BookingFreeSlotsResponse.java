package dev.esanchez.timely.backend.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public class BookingFreeSlotsResponse {

    private OffsetDateTime startDatetime;
    private OffsetDateTime endDatetime;
    private Long duration;

    public BookingFreeSlotsResponse(OffsetDateTime startDatetime, OffsetDateTime endDatetime, Long duration) {
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.duration = duration;
    }

    public BookingFreeSlotsResponse(List <BookingFreeSlotsResponse  > bookingFreeSlotsResponses) {

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

    public Long getDuration() {
        return duration;
    }
    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
