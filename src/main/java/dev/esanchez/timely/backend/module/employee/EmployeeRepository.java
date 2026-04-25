package dev.esanchez.timely.backend.module.employee;

import dev.esanchez.timely.backend.module.business.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<List<Employee>> findAllByBusiness(Business business);

}
