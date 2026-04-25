package dev.esanchez.timely.backend.module.employee;

import dev.esanchez.timely.backend.core.globalException.customGlobalException.NotFoundException;
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

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final EmployeeRepository employeeRepository;

    public List<EmployeeResponse> getAllEmployees(String emailBusinessOwner) {

        User user = userRepository.findByEmail(emailBusinessOwner).orElseThrow(()-> new NotFoundException("User"));
        Business business = businessRepository.findByUser(user).orElseThrow(()-> new NotFoundException("Business"));
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
