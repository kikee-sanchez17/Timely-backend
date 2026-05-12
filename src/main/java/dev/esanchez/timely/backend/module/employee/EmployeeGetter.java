package dev.esanchez.timely.backend.module.employee;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
import dev.esanchez.timely.backend.core.security.AuthenticationFacade;
import dev.esanchez.timely.backend.module.business.Business;
import dev.esanchez.timely.backend.module.business.BusinessRepository;
import dev.esanchez.timely.backend.module.employee.dto.response.EmployeeResponse;
import dev.esanchez.timely.backend.module.identity.User;
import dev.esanchez.timely.backend.module.identity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmployeeGetter {

    private final AuthenticationFacade authenticationFacade;
    private final EmployeeRepository employeeRepository;

    public List<EmployeeResponse> getAllEmployees() {

        Business business = authenticationFacade.getCurrentBusiness();

        return employeeRepository.findAllByBusiness(business)
                .orElseThrow(() -> new NotFoundException("Employees"))
                .stream()
                .map(employee -> EmployeeResponse.builder()
                        .employeeId(employee.getEmployeeId())
                        .name(employee.getName())
                        .surname(employee.getSurname())
                        .isActive(employee.getIsActive())
                        .createdAt(employee.getCreatedAt())
                        .build())
                .toList();
    }
}
