package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.repository.query.Param;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorStaffDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistStaffDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.StaffResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.List;

public interface StaffService {
    List<StaffResponse> findStaff(String roleName, String filterKey);

    StaffResponse findStaffById(Long id);

    User  findDoctorById(Long id);

    User findReceptionistById(Long id);

    DoctorStaffDetailResponse findDoctorStaffDetailById(Long id);
    ReceptionistStaffDetailResponse findReceptionistStaffDetailById(Long id);

    Long countDoctorsAppointmentByAppointmentStatus(String appointmentStatus, Long doctorId);
}
