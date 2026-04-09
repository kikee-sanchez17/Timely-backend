package dev.esanchez.timely.backend.repository;

import dev.esanchez.timely.backend.entity.BusinessExceptionInterval;
import dev.esanchez.timely.backend.entity.EmployeeExceptionInterval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BusinessExceptionIntervalRepository extends JpaRepository<BusinessExceptionInterval, Long> {

    List<BusinessExceptionInterval> findByBusiness_BusinessIdAndDate(Long id, LocalDate date);

}
