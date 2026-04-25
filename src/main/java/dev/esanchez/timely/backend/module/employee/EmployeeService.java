package dev.esanchez.timely.backend.module.employee;

import dev.esanchez.timely.backend.module.employee.dto.request.CreateEmployeeRequest;
import dev.esanchez.timely.backend.module.employee.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {

    void createEmployee(CreateEmployeeRequest createEmployeeRequest , String emailBusinessOwner);

    List<EmployeeResponse> getAllEmployees(String emailBusinessOwner);
}
