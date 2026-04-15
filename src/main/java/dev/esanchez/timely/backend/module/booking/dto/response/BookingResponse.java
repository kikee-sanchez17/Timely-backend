package dev.esanchez.timely.backend.module.booking.dto.response;

import java.time.OffsetDateTime;

public class BookingResponse {

    private OffsetDateTime startDatetime;
    private Long subserviceId;
    private Long customerUserId;
    private Long employeeId;

    public BookingResponse(OffsetDateTime startDatetime,Long subserviceId, Long customerUserId, Long employeeId) {
        this.startDatetime = startDatetime;
        this.subserviceId = subserviceId;
        this.customerUserId = customerUserId;
        this.employeeId = employeeId;
    }

    public OffsetDateTime getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(OffsetDateTime startDatetime) {
        this.startDatetime = startDatetime;
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

}