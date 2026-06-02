package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.repository.query.Param;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorOnDutyResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.DoctorSchedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    List<DoctorOnDutyResponse> findDoctorScheduleByDate(@Param("date") LocalDate date);

}
