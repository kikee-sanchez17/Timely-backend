package dev.esanchez.timely.backend.module.schedules.employee.exception;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeExceptionIntervalRepository extends JpaRepository<EmployeeExceptionInterval, Long> {

    List <EmployeeExceptionInterval> findByEmployee_EmployeeIdAndDate(Long id, LocalDate date);


}
