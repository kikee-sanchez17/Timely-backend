package dev.esanchez.timely.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeExceptionInterval extends JpaRepository<BusinessExceptionInterval, Long> {

    List<EmployeeExceptionInterval> findByIdAndDate(Long id, LocalDate date);
    
}
