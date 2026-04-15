package dev.esanchez.timely.backend.module.schedules.business;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface BusinessRepository extends JpaRepository<Business, Long> {

}
