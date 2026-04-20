package dev.esanchez.timely.backend.module.employee.exception;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EmployeeScheduleExceptionRepository extends JpaRepository<EmployeeScheduleException, Long> {

    Optional<EmployeeScheduleException> findByEmployee_EmployeeIdAndDate(Long id, LocalDate date);
}
