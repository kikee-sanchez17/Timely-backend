package dev.esanchez.timely.backend.module.schedules.business;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessScheduleRepository extends JpaRepository<BusinessSchedule,Long> {

    //SELECT * FROM BusinessSchedule JOIN business.id = id
    List<BusinessSchedule> findAllByBusiness_businessIdAndDayOfWeek(Long business_id, int dayOfWeek);

}
