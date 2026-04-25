package dev.esanchez.timely.backend.module.employee;

import dev.esanchez.timely.backend.module.employee.creator.EmployeeCreator;
import dev.esanchez.timely.backend.module.employee.dto.request.CreateEmployeeRequest;
import dev.esanchez.timely.backend.module.employee.dto.response.EmployeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeCreator employeeCreator;
    private final EmployeeGetter employeeGetter;


    @Override
    public void createEmployee(CreateEmployeeRequest createEmployeeRequest, String emailBusinessOwner) {
        employeeCreator.create(createEmployeeRequest, emailBusinessOwner);
    }

    @Override
    public List<EmployeeResponse> getAllEmployees(String emailBusinessOwner) {

        return employeeGetter.getAllEmployees(emailBusinessOwner);
    }
}
