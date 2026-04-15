package dev.esanchez.timely.backend.module.booking.dto.request;

import java.time.OffsetDateTime;

public class CreateBookingRequest {

    private OffsetDateTime startDatetime;
    private OffsetDateTime endDatetime;
    private Long subserviceId;
    private Long customerUserId;
    private Long employeeId;
    private String notes;

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

    public Long getSubserviceId() {
        return subserviceId;
    }

    public void setSubserviceId(Long subserviceId) {
        this.subserviceId = subserviceId;
    }

    public Long getCustomerUserId() {
        return customerUserId;
    }

    public void setCustomerUserId(Long customerUserId) {
        this.customerUserId = customerUserId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}