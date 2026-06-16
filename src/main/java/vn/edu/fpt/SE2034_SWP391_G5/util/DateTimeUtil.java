package vn.edu.fpt.SE2034_SWP391_G5.util;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
@Component
public class DateTimeUtil {

    public LocalDate getStartWeekDate(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public LocalDate getEndWeekDate(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    }
}
