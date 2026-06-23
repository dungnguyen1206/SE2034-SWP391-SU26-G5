package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.entity.WeekSchedule;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.WeekScheduleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.WeekScheduleService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeekScheduleServiceImpl implements WeekScheduleService {

    private final WeekScheduleRepository weekScheduleRepository;

    @Override
    public WeekSchedule findWeekScheduleById(Long id) {
      return  weekScheduleRepository.findWeekScheduleById(id);
    }


    /*
    *
    * this function helps to generate day of week
    *
    * */
    @Override
    public List<LocalDate> workdates(Long weekScheduleId) {
        WeekSchedule weekSchedule = weekScheduleRepository.findWeekScheduleById(weekScheduleId);
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i <7; i++) {
            dates.add(weekSchedule.getStartDate().plusDays(i));
        }
        return dates;
    }

    @Override
    public List<WeekSchedule> getAllWeekSchedules() {
        return weekScheduleRepository.findAll();
    }

    @Override
    public WeekSchedule findPresentWeekSchedule() {
       LocalDate today = LocalDate.now();
        WeekSchedule weekSchedule =weekScheduleRepository.findWeekScheduleByStartDate(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
        return weekSchedule;
    }

    @Override
    public WeekSchedule findNextWeekSchedule(WeekSchedule presentWeekSchedule) {
       WeekSchedule nextWeekSchedule = weekScheduleRepository.findNextWeekSchedule(presentWeekSchedule.getEndDate());
       return nextWeekSchedule;
    }

    @Override
    public WeekSchedule findPreviousWeekSchedule(WeekSchedule presentWeekSchedule) {
        WeekSchedule previousWeekSchedule = weekScheduleRepository.findPreviousWeekSchedule(presentWeekSchedule.getStartDate());
        return previousWeekSchedule;
    }


}
