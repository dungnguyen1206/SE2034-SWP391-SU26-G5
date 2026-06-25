package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateDoctorScheduleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.DoctorScheduleUpdateRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.*;

import java.time.LocalDate;
import java.util.List;

import vn.edu.fpt.SE2034_SWP391_G5.entity.DoctorSchedule;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Room;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.WeekSchedule;


public interface ScheduleService {
    List<DoctorOnDutyResponse> findDoctorScheduleByDate(LocalDate date);
    List<DoctorScheduleWeekResponse> getWeeklySchedule(Long doctorId, LocalDate targetDate);
    DoctorScheduleResponse createDoctorSchedule(CreateDoctorScheduleRequest createDoctorScheduleRequest,Long userId,Long weekScheduleId);
    List<Room> getAllRoomsByDepartmentId(Integer departmentId);
    List<User> getAllDoctorByDepartmentId(Integer departmentId);
    DoctorScheduleReportResponse getWeeklyScheduleReport(Long doctorId, LocalDate targetDate);
    Page<DoctorScheduleRowResponse> doctorScheduleRowResponse(Long weekScheduleId, Integer departmentId, String doctorName, String shift, int page, int size);
    WeekSchedule updateWeekSchedule(Long weekScheduleId,String action, Long managerId);
    DoctorScheduleUpdateRequest getDoctorScheduleUpdateRequest(Long doctorScheduleId);
    DoctorScheduleUpdateRequest updateDoctorSchedule(DoctorScheduleUpdateRequest doctorScheduleUpdateRequest,Long  weekScheduleId);
}
