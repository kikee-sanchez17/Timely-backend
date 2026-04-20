package dev.esanchez.timely.backend.module.business.exception;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;


@Repository
public interface BusinessScheduleExceptionRepository extends JpaRepository<BusinessScheduleException, Long> {

   Optional<BusinessScheduleException> findByBusiness_BusinessIdAndDate(Long id, LocalDate date);

}
