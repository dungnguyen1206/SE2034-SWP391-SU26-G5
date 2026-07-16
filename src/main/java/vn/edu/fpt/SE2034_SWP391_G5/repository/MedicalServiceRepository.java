package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.MedicalServiceResponseForManager;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {

    List<MedicalService> findByDepartmentIdAndStatus(Integer departmentId, String status);
    List<MedicalService> findByStatus(String status);
    
    // Tìm dịch vụ khám lâm sàng mặc định cho department
    Optional<MedicalService> findFirstByDepartmentIdAndNameContainingIgnoreCaseAndStatus(
            Integer departmentId, String name, String status);

    @Query("select ms from MedicalService ms left join fetch ms.department d" +
            " where (:filterKey is null or ms.name like :filterKey )" +
            " and (:departmentId is null or d.id=:departmentId)")
    Page<MedicalService> getMedicalServicesByFilter(@Param("filterKey") String filterKey, @Param("departmentId") Integer departmentId, Pageable pageable);


    @Query("Select ms from MedicalService  ms join fetch ms.department d where ms.id=:id")
    Optional<MedicalService> findMedicalServiceById(@Param("id") Long id);


}
