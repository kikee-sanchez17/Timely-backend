package dev.esanchez.timely.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BusinessExceptionInterval extends JpaRepository<BusinessExceptionInterval, Long> {

    List<BusinessExceptionInterval> findByIdAndDate(Long id, LocalDate date);
}
