package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.module.business.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service,Long> {

    Optional<List<Service>> findAllByBusiness(Business business);
}
