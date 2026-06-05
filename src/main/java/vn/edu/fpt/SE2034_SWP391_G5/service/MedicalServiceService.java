package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;

import java.util.List;

public interface MedicalServiceService {

    List<MedicalService> getMedicalServicelistByDepartment(Integer departmentId);

}
