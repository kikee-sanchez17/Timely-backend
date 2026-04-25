package dev.esanchez.timely.backend.module.employee;

import dev.esanchez.timely.backend.core.security.CustomUserDetails;
import dev.esanchez.timely.backend.module.employee.dto.request.CreateEmployeeRequest;
import dev.esanchez.timely.backend.module.employee.dto.response.EmployeeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/create")
    public void createEmployee(
            @RequestBody @Valid CreateEmployeeRequest createEmployeeRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        System.out.println("Roles: "+customUserDetails.getAuthorities());
        employeeService.createEmployee(createEmployeeRequest,customUserDetails.getUsername());
    }

    @GetMapping("/getAllEmployees")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(employeeService.getAllEmployees(customUserDetails.getUsername()));
    }
}
