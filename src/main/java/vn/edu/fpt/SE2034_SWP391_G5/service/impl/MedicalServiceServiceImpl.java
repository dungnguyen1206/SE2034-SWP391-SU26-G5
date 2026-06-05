package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalServiceService;
import lombok.RequiredArgsConstructor;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceRepository;


import java.util.List;
@Service
@RequiredArgsConstructor
public class MedicalServiceServiceImpl implements MedicalServiceService {
    private final MedicalServiceRepository medicalServiceRepository;


    @Override
    public List<MedicalService> getMedicalServicelistByDepartment(Integer departmentId) {
        // Trước: luôn gọi findByDepartmentIdAndStatus dù departmentId null → query WHERE department_id = NULL → trống
        if (departmentId == null) {
            return medicalServiceRepository.findByStatus("ACTIVE");
        }
        return medicalServiceRepository.findByDepartmentIdAndStatus(departmentId, "ACTIVE");
    }
    

}
