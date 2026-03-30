package dev.esanchez.timely.backend.repository;
import dev.esanchez.timely.backend.entity.EmployeeSubservice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.*;

@Repository
public interface EmployeeSubserviceRepository extends JpaRepository<EmployeeSubservice, Long> {

}
