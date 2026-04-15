package dev.esanchez.timely.backend.module.booking.dto.request;

import java.time.LocalDate;

public class AvailableSlotRequest {

    private Long customerUserId;
    private Long subserviceId;
    private Long employeeId;
    private LocalDate date;
    //2026-04-25

    public Long getCustomerUserId() {
        return customerUserId;
    }

    public void setCustomerUserId(Long customerUserId) {
        this.customerUserId = customerUserId;
    }
    public Long getSubserviceId() {
        return subserviceId;

    }
    public void setSubserviceId(Long subserviceId) {
        this.subserviceId = subserviceId;
    }
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }



}
