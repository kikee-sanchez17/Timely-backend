package dev.esanchez.timely.backend.repository;

import dev.esanchez.timely.backend.entity.EmployeeExceptionInterval;
import dev.esanchez.timely.backend.entity.EmployeeScheduleException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeExceptionIntervalRepository extends JpaRepository<EmployeeExceptionInterval, Long> {

    List <EmployeeExceptionInterval> findByEmployee_EmployeeIdAndDate(Long id, LocalDate date);


}
