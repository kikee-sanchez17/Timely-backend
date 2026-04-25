package dev.esanchez.timely.backend.module.employee.creator;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.business.BusinessRepository;
import dev.esanchez.timely.backend.module.employee.Employee;
import dev.esanchez.timely.backend.module.employee.EmployeeRepository;
import dev.esanchez.timely.backend.module.employee.dto.request.CreateEmployeeRequest;
import dev.esanchez.timely.backend.module.identity.User;
import dev.esanchez.timely.backend.module.identity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeCreator {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final EmployeeRepository employeeRepository;

    public void create(CreateEmployeeRequest createEmployeeRequest, String emailBusinessOwner){

        User user = userRepository.findByEmail(emailBusinessOwner).orElseThrow(()->new NotFoundException("User"));
        Business business = businessRepository.findByUser(user).orElseThrow(()->new NotFoundException("Business"));

        Employee employee = Employee.builder()
                .name(createEmployeeRequest.getName())
                .surname(createEmployeeRequest.getSurname())
                .business(business)
                .isActive(true)
                .build();

        employeeRepository.save(employee);
    }

}
