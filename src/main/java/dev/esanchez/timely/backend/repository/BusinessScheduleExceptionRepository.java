package dev.esanchez.timely.backend.repository;


import dev.esanchez.timely.backend.entity.BusinessScheduleException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface BusinessScheduleExceptionRepository extends JpaRepository<BusinessScheduleException, Long> {

    BusinessScheduleException findByIdAndDate(Long id, LocalDate date);

}
