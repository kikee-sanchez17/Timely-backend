package dev.esanchez.timely.backend.entity;

import jakarta.persistence.*;

@Entity
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

    public EmployeeSubservice() {
    }

    public EmployeeSubservice(Employee employee, Subservice subservice) {
        this.employee = employee;
        this.subservice = subservice;
        this.id = new EmployeeSubserviceId(
                subservice.getSubserviceId(),
                employee.getEmployeeId()
        );
    }

    public Employee getEmployee() {
        return employee;
    }

    public Subservice getSubservice() {
        return subservice;
    }

    public EmployeeSubserviceId getId() {
        return id;
    }
}