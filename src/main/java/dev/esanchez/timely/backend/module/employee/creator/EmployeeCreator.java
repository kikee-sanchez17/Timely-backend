package dev.esanchez.timely.backend.module.employee.creator;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.core.security.AuthenticationFacade;
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

    private final EmployeeRepository employeeRepository;
    private final AuthenticationFacade authenticationFacade;

    public void create(CreateEmployeeRequest createEmployeeRequest){

        Business business = authenticationFacade.getCurrentBusiness();

        Employee employee = Employee.builder()
                .name(createEmployeeRequest.getName())
                .surname(createEmployeeRequest.getSurname())
                .business(business)
                .isActive(true)
                .build();

        employeeRepository.save(employee);
    }

}
