package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.module.employee.Employee;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employee_subservices")
public class EmployeeSubservice {

    @EmbeddedId
    private EmployeeSubserviceId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("subserviceId")
    @JoinColumn(name = "subservice_id", nullable = false)
    private Subservice subservice;

}