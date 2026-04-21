package dev.esanchez.timely.backend.module.services;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSubserviceId implements Serializable {

    private Long subserviceId;
    private Long employeeId;

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