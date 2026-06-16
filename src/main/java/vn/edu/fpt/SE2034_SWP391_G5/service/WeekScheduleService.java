package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.entity.WeekSchedule;

import java.time.LocalDate;
import java.util.List;

public interface WeekScheduleService {

    WeekSchedule findWeekScheduleById(Long id);
    List<LocalDate> workdates(Long weekScheduleId);

    List<WeekSchedule> getAllWeekSchedules();

    WeekSchedule findPresentWeekSchedule();

    WeekSchedule findNextWeekSchedule(WeekSchedule presentWeekSchedule);
    WeekSchedule findPreviousWeekSchedule(WeekSchedule presentWeekSchedule);
}
