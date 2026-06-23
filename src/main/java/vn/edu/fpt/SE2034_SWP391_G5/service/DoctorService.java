package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorResponse;
import java.util.List;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;


public interface DoctorService {
    List<User> findByDoctorStatus(String doctorStatus);
    List<User> findByRoleNameAndStatus(String roleName, String status);
    List<DoctorResponse> getDoctorsByDepartment(Integer departmentId);
    DoctorResponse getDoctorById(Long doctorId);

    List<DoctorResponse> getAllDoctors();
}
