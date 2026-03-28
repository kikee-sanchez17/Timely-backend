package dev.esanchez.timely.backend.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EmployeeSubserviceId implements Serializable {

    private Long subserviceId;
    private Long employeeId;

    public EmployeeSubserviceId() {
    }

    public EmployeeSubserviceId(Long subserviceId, Long employeeId) {
        this.subserviceId = subserviceId;
        this.employeeId = employeeId;
    }

    public Long getSubserviceId() {
        return subserviceId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeSubserviceId)) return false;
        EmployeeSubserviceId that = (EmployeeSubserviceId) o;
        return Objects.equals(subserviceId, that.subserviceId) &&
                Objects.equals(employeeId, that.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subserviceId, employeeId);
    }
}