package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    Optional<Appointment> findByAppointmentCode(String appointmentCode);

    boolean existsBySlotIdAndPatientIdAndStatusNotIn(Long slotId, Long patientId, List<String> excludedStatuses);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId AND a.status = :status")
    long countByPatientIdAndStatus(@Param("patientId") Long patientId, @Param("status") String status);


    //LinhNH 1/6/2026
    Page<Appointment> findByDoctorIdAndStatusIn(Long doctorId, List<String> statuses, Pageable pageable);

    long countByDoctorIdAndStatus(Long doctorId, String status);

    long countByDoctorIdAndStatusIn(Long doctorId, List<String> statuses);
}

