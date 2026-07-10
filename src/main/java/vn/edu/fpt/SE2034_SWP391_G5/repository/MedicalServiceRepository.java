package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {

    List<MedicalService> findByDepartmentIdAndStatus(Integer departmentId, String status);
    List<MedicalService> findByStatus(String status);
    Optional<MedicalService> findMedicalServiceById(Long id);
}
