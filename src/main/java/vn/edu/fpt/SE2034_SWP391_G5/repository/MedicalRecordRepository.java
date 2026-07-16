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
