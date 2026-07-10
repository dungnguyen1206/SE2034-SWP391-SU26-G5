package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateMedicalServiceRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.MedicalServiceResponseForManager;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;

import java.util.List;

public interface MedicalServiceService {

    List<MedicalService> getMedicalServicelistByDepartment(Integer departmentId);
    Page<MedicalServiceResponseForManager> getMedicalServiceResponsesByFilter(String filterKey, Integer departmentId, int page, int size);

    UpdateMedicalServiceRequest getMedicalServiceById(Long id);

    UpdateMedicalServiceRequest saveMedicalServiceRequest(UpdateMedicalServiceRequest updateMedicalServiceRequest);
}
