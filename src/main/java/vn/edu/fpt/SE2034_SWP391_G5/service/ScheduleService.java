package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.repository.query.Param;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorOnDutyResponse;

import java.time.LocalDate;
import java.util.List;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorScheduleReportResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorScheduleWeekResponse;

public interface ScheduleService {
    List<DoctorOnDutyResponse> findDoctorScheduleByDate(@Param("date") LocalDate date);
    List<DoctorScheduleWeekResponse> getWeeklySchedule(Long doctorId, LocalDate targetDate);
    DoctorScheduleReportResponse getWeeklyScheduleReport(Long doctorId, LocalDate targetDate);
}
