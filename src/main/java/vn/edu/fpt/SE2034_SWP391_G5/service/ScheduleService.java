package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.repository.query.Param;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateDoctorScheduleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorOnDutyResponse;

import java.time.LocalDate;
import java.util.List;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorScheduleResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorScheduleWeekResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Room;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

public interface ScheduleService {
    List<DoctorOnDutyResponse> findDoctorScheduleByDate(LocalDate date);
    List<DoctorScheduleWeekResponse> getWeeklySchedule(Long doctorId, LocalDate targetDate);
    DoctorScheduleResponse createDoctorSchedule(CreateDoctorScheduleRequest createDoctorScheduleRequest,Long userId,Long weekScheduleId);
    List<Room> getAllRoomsByDepartmentId(Integer departmentId);
    List<User> getAllDoctorByDepartmentId(Integer departmentId);

}
