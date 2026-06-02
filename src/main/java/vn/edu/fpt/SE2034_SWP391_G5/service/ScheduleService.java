package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorScheduleWeekResponse;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    List<DoctorScheduleWeekResponse> getWeeklySchedule(Long doctorId, LocalDate targetDate);
}
