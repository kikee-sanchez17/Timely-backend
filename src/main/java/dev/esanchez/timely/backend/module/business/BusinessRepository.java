package dev.esanchez.timely.backend.module.business;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface BusinessRepository extends JpaRepository<Business, Long> {

}
