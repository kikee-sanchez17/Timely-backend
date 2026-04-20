package dev.esanchez.timely.backend.module.business;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessScheduleRepository extends JpaRepository<BusinessSchedule,Long> {

    //SELECT * FROM BusinessSchedule JOIN business.id = id
    List<BusinessSchedule> findAllByBusiness_businessIdAndDayOfWeek(Long business_id, int dayOfWeek);

}
