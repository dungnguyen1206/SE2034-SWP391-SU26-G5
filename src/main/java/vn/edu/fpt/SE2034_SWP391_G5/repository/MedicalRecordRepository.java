package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByPatientIdOrderByExaminationDateDesc(Long patientId);

    @Query("SELECT DISTINCT mr FROM MedicalRecord mr " +
           "LEFT JOIN FETCH mr.doctor d " +
           "LEFT JOIN FETCH mr.appointment a " +
           "LEFT JOIN FETCH a.service " +
           "LEFT JOIN FETCH mr.medicalServiceOrders mso " +
           "LEFT JOIN FETCH mso.medicalService " +
           "WHERE mr.patient.id = :patientId " +
           "AND mr.status = 'FINALIZED' " +
           "ORDER BY mr.examinationDate DESC")
    List<MedicalRecord> findByPatientIdWithDetails(@Param("patientId") Long patientId);

    @Query(value = "SELECT mr FROM MedicalRecord mr " +
           "LEFT JOIN FETCH mr.doctor d " +
           "LEFT JOIN FETCH mr.appointment a " +
           "LEFT JOIN FETCH a.service " +
           "WHERE mr.patient.id = :patientId " +
           "AND mr.status = 'FINALIZED' " +
           "AND (:departmentName IS NULL OR d.department.name = :departmentName) " +
           "ORDER BY mr.examinationDate DESC",
           countQuery = "SELECT count(mr) FROM MedicalRecord mr WHERE mr.patient.id = :patientId AND mr.status = 'FINALIZED' AND (:departmentName IS NULL OR mr.doctor.department.name = :departmentName)")
    org.springframework.data.domain.Page<MedicalRecord> findByPatientIdWithDetailsPaginated(@Param("patientId") Long patientId, @Param("departmentName") String departmentName, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT mr FROM MedicalRecord mr " +
           "LEFT JOIN FETCH mr.medicalServiceOrders mso " +
           "LEFT JOIN FETCH mso.medicalService " +
           "WHERE mr.appointment.id = :appointmentId")
    Optional<MedicalRecord> findByAppointmentId(@Param("appointmentId") Long appointmentId);

    @Query("SELECT COUNT(mr) FROM MedicalRecord mr WHERE mr.doctor.id = :doctorId " +
           "AND mr.examinationDate >= :start AND mr.examinationDate < :end " +
           "AND mr.prescriptionText IS NOT NULL AND mr.prescriptionText <> ''")
    long countPrescriptionsByDoctorAndDate(@Param("doctorId") Long doctorId,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
}
