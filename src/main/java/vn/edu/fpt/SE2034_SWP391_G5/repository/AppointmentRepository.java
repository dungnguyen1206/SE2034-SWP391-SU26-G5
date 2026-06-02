package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient " +
            "LEFT JOIN FETCH a.doctor " +
            "LEFT JOIN FETCH a.service sv " +
            "LEFT JOIN FETCH sv.department " +
            "LEFT JOIN FETCH a.slot sl " +
            "LEFT JOIN FETCH sl.schedule sch " +
            "LEFT JOIN FETCH sch.room " +
            "ORDER BY a.bookingDate DESC, sl.startTime ASC")
    List<Appointment> findAllForReceptionistList();

    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient " +
            "LEFT JOIN FETCH a.doctor " +
            "LEFT JOIN FETCH a.service sv " +
            "LEFT JOIN FETCH sv.department " +
            "LEFT JOIN FETCH a.slot sl " +
            "LEFT JOIN FETCH sl.schedule sch " +
            "LEFT JOIN FETCH sch.room " +
            "WHERE a.id = :appointmentId")
    Optional<Appointment> findCheckInTicketById(@Param("appointmentId") Long appointmentId);

    @Query("SELECT COUNT(a2) FROM Appointment a2 " +
            "JOIN a2.slot sl2 " +
            "JOIN sl2.schedule sch2 " +
            "JOIN sch2.room r2 " +
            "WHERE a2.bookingDate = :bookingDate " +
            "AND a2.checkInTime IS NOT NULL " +
            "AND r2.id = ( " +
            "   SELECT r.id FROM Appointment a " +
            "   JOIN a.slot sl " +
            "   JOIN sl.schedule sch " +
            "   JOIN sch.room r " +
            "   WHERE a.id = :appointmentId " +
            ") " +
            "AND ( " +
            "   a2.checkInTime < :checkInTime " +
            "   OR (a2.checkInTime = :checkInTime AND a2.id <= :appointmentId) " +
            ")")
    Long countQueueNumberForTicket(@Param("appointmentId") Long appointmentId, @Param("bookingDate") LocalDate bookingDate, @Param("checkInTime") LocalDateTime checkInTime);

    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient p " +
            "LEFT JOIN FETCH p.addresses " +
            "LEFT JOIN FETCH a.doctor " +
            "LEFT JOIN FETCH a.service sv " +
            "LEFT JOIN FETCH sv.department " +
            "LEFT JOIN FETCH a.slot sl " +
            "LEFT JOIN FETCH sl.schedule sch " +
            "LEFT JOIN FETCH sch.room " +
            "WHERE a.id = :appointmentId")
    Optional<Appointment> findAppointmentDetailById(@Param("appointmentId") Long appointmentId);

    List<Appointment> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    Optional<Appointment> findByAppointmentCode(String appointmentCode);

    boolean existsBySlotIdAndPatientIdAndStatusNotIn(Long slotId, Long patientId, List<String> excludedStatuses);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId AND a.status = :status")
    long countByPatientIdAndStatus(@Param("patientId") Long patientId, @Param("status") String status);
}