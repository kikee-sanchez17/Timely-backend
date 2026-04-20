package dev.esanchez.timely.backend.module.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface EmployeeScheduleRepository extends JpaRepository<EmployeeSchedule, Long> {

    List<EmployeeSchedule> findAllByEmployee_employeeIdAndDayOfWeek(Long employee_id, int dayOfWeek);

    boolean existsByEmployee_employeeIdAndDayOfWeek(Long employeeId, Integer dayOfWeek);
}
