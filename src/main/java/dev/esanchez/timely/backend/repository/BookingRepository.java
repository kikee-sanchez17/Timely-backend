package dev.esanchez.timely.backend.repository;
import dev.esanchez.timely.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByEmployeeEmployeeIdAndStartDatetimeGreaterThanEqualAndStartDatetimeLessThanOrderByStartDatetimeAsc(
            Long employeeId,
            OffsetDateTime startOfDay,
            OffsetDateTime startOfNextDay
    );
}
