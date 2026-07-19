package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateMedicalServiceRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.MedicalServiceResponseForManager;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.service.ImageUploadService;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalServiceService;
import lombok.RequiredArgsConstructor;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceRepository;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalServiceServiceImpl implements MedicalServiceService {
    private final MedicalServiceRepository medicalServiceRepository;
    private final ImageUploadService imageUploadService;


    @Override
    public List<MedicalService> getMedicalServicelistByDepartment(Integer departmentId) {
        // Trước: luôn gọi findByDepartmentIdAndStatus dù departmentId null → query WHERE department_id = NULL → trống
        if (departmentId == null) {
            return medicalServiceRepository.findByStatus("ACTIVE");
        }
        return medicalServiceRepository.findByDepartmentIdAndStatus(departmentId, "ACTIVE");
    }

    @Override
    public Page<MedicalServiceResponseForManager> getMedicalServiceResponsesByFilter(String filterKey, Integer departmentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (filterKey != null && filterKey.trim().isEmpty()) {
            filterKey = null;
        }
        if (filterKey != null && !filterKey.trim().isEmpty()) {
            filterKey = "%" + filterKey.trim() + "%";
        }
        return medicalServiceRepository.getMedicalServicesByFilter(filterKey, departmentId, pageable).map(this::getMedicalServiceResponseForManager);
    }

    @Override
    public UpdateMedicalServiceRequest getMedicalServiceById(Long id) {
        MedicalService medicalService = medicalServiceRepository.findMedicalServiceById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ y tế"));
        return UpdateMedicalServiceRequest.builder()
                .serviceId(medicalService.getId())
                .medicalServiceName(medicalService.getName())
                .department(medicalService.getDepartment())
                .price(medicalService.getReferencePrice())
                .description(medicalService.getDescription())
                .duration(medicalService.getEstimatedDuration())
                .imageUrl(medicalService.getImageUrl())
                .status(medicalService.getStatus()).build();
    }

    @Override
    public UpdateMedicalServiceRequest saveMedicalServiceRequest(UpdateMedicalServiceRequest updateMedicalServiceRequest) {
        MedicalService medicalService = medicalServiceRepository.findMedicalServiceById(updateMedicalServiceRequest.getServiceId()).orElseThrow(() -> new ResourceNotFoundException("không tìm thấy dịch vụ y tế"));
        medicalService.setDescription(updateMedicalServiceRequest.getDescription());
        medicalService.setReferencePrice(updateMedicalServiceRequest.getPrice());
        medicalService.setEstimatedDuration(updateMedicalServiceRequest.getDuration());
        if (updateMedicalServiceRequest.getImageUrl() != null) {
            imageUploadService.deleteImage(medicalService.getImageUrl());
            medicalService.setImageUrl(updateMedicalServiceRequest.getImageUrl());
        }
        medicalService.setStatus(updateMedicalServiceRequest.getStatus());
        medicalService.setUpdatedAt(LocalDateTime.now());
        medicalServiceRepository.save(medicalService);
        updateMedicalServiceRequest.setMedicalServiceName(medicalService.getName());
        updateMedicalServiceRequest.setDepartment(medicalService.getDepartment());
        return updateMedicalServiceRequest;
    }

    private MedicalServiceResponseForManager getMedicalServiceResponseForManager(MedicalService medicalService) {
        return MedicalServiceResponseForManager.builder()
                .id(medicalService.getId())
                .serviceName(medicalService.getName())
                .department(medicalService.getDepartment())
                .servicePrice(medicalService.getReferencePrice())
                .timeDuration(medicalService.getEstimatedDuration())
                .status(medicalService.getStatus())
                .imageUrl(medicalService.getImageUrl())
                .build();
    }


}
