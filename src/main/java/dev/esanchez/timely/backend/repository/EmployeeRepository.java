package dev.esanchez.timely.backend.repository;
import dev.esanchez.timely.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.*;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
