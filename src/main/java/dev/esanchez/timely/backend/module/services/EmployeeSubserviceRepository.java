package dev.esanchez.timely.backend.module.services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.*;

@Repository
public interface EmployeeSubserviceRepository extends JpaRepository<EmployeeSubservice, Long> {

}
