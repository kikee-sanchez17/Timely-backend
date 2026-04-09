package dev.esanchez.timely.backend.repository;

import dev.esanchez.timely.backend.entity.BusinessSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessScheduleRepository extends JpaRepository<BusinessSchedule,Long> {

    //SELECT * FROM BusinessSchedule JOIN business.id = id
 List<BusinessSchedule> findAllByBusiness_businessIdAndDayOfWeek(Long business_id, int dayOfWeek);

}
