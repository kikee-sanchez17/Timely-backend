package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.module.business.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubserviceRepository  extends JpaRepository<Subservice, Long> {

    Optional<List<Subservice>> findAllByServiceId(long id);


}
