package dev.esanchez.timely.backend.repository;

import dev.esanchez.timely.backend.entity.Subservice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.*;

@Repository
public interface SubserviceRepository  extends JpaRepository<Subservice, Long> {
}
