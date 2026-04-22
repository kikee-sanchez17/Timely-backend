package dev.esanchez.timely.backend.module.booking.availability;

import dev.esanchez.timely.backend.module.business.exception.BusinessExceptionInterval;
import dev.esanchez.timely.backend.module.employee.exception.EmployeeExceptionInterval;
import dev.esanchez.timely.backend.module.shared.ExceptionIntervalType;
import dev.esanchez.timely.backend.module.shared.TimeBlock;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExceptionIntervalBlockFactory {

    public List<TimeBlock> fromBusinessIntervals(
            List<BusinessExceptionInterval> intervals,
            ExceptionIntervalType type,
            LocalDate date,
            ZoneId zoneId
    ) {
        List<TimeBlock> result = new ArrayList<>();

        for (BusinessExceptionInterval interval : intervals) {
            if (interval.getIntervalType() == type) {
                result.add(TimeBlockMapper.toTimeBlock(date, interval.getStartTime(), interval.getEndTime(), zoneId));
            }
        }

        return result;
    }

    public List<TimeBlock> fromEmployeeIntervals(
            List<EmployeeExceptionInterval> intervals,
            ExceptionIntervalType type,
            LocalDate date,
            ZoneId zoneId
    ) {
        List<TimeBlock> result = new ArrayList<>();

        for (EmployeeExceptionInterval interval : intervals) {
            if (interval.getIntervalType() == type) {
                result.add(TimeBlockMapper.toTimeBlock(date, interval.getStartTime(), interval.getEndTime(), zoneId));
            }
        }

        return result;
    }
}
