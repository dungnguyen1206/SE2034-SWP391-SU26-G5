package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorResponse;

import java.util.List;

public interface DoctorService {

    List<DoctorResponse> getDoctorsByDepartment(Integer departmentId);

    DoctorResponse getDoctorById(Long doctorId);
}
