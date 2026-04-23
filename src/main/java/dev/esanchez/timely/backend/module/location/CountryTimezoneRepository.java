package dev.esanchez.timely.backend.module.location;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryTimezoneRepository extends JpaRepository<CountryTimezone, Long> {

    Optional<CountryTimezone> findById(CountryTimezoneId countryTimezoneId);
}
