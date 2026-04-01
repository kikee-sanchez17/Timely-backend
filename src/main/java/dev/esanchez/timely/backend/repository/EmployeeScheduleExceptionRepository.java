package dev.esanchez.timely.backend.repository;

import dev.esanchez.timely.backend.entity.EmployeeScheduleException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EmployeeScheduleExceptionRepository extends JpaRepository<EmployeeScheduleException, Long> {

    EmployeeScheduleException findByIdAndDate(Long id, LocalDate date);
}
