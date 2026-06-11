package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentStatusCountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT new vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentStatusCountResponse(a.status, count (a)) " +
            " from Appointment a WHere a.bookingDate= :date group by a.status")
    List<AppointmentStatusCountResponse> findTodayAppointmentsByStatus(@Param("date") LocalDate date);

    @Query("SELECT a from Appointment a " +
            "JOIN fetch a.patient p " +
            "JOIN fetch a.doctor d " +
            "JOIN fetch a.service s " +
            "join fetch a.slot sl " +
            "join fetch sl.schedule ds " +
            "join fetch ds.room r " +
            "WHERE a.bookingDate =:today " +
            "order by sl.startTime asc")
    List<Appointment> findAppointmentsByBookingDate(@Param("today") LocalDate today);

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

    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.slot sl " +
            "LEFT JOIN FETCH sl.schedule sch " +
            "LEFT JOIN FETCH sch.room r " +
            "WHERE a.bookingDate = :bookingDate " +
            "AND r.id = :roomId " +
            "AND a.checkInTime IS NOT NULL " +
            "AND a.status IN ('WAITING', 'EXAMINING', 'COMPLETED') " +
            "ORDER BY a.checkInTime ASC, a.id ASC")
    List<Appointment> findCheckedInAppointmentsByBookingDateAndScheduleId(
            @Param("bookingDate") LocalDate bookingDate,
            @Param("roomId") Integer roomId
    );

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

    // Fetch appointment cùng slot và schedule để tránh LazyInitializationException
    // Trước đây: chỉ dùng findByPatientIdOrderByCreatedAtDesc không JOIN FETCH
    @Query("SELECT DISTINCT a FROM Appointment a " +
            "LEFT JOIN FETCH a.slot sl " +
            "LEFT JOIN FETCH sl.schedule sc " +
            "LEFT JOIN FETCH sc.room " +
            "LEFT JOIN FETCH a.doctor d " +
            "LEFT JOIN FETCH d.department " +
            "LEFT JOIN FETCH a.service " +
            "WHERE a.patient.id = :patientId " +
            "ORDER BY a.createdAt DESC")
    List<Appointment> findByPatientIdWithDetails(@Param("patientId") Long patientId);

    Optional<Appointment> findByAppointmentCode(String appointmentCode);

    boolean existsBySlotIdAndPatientIdAndStatusNotIn(Long slotId, Long patientId, List<String> excludedStatuses);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId AND a.status = :status")
    long countByPatientIdAndStatus(@Param("patientId") Long patientId, @Param("status") String status);


    //LinhNH 1/6/2026
    Page<Appointment> findByDoctorIdAndStatusIn(Long doctorId, List<String> statuses, Pageable pageable);

    long countByDoctorIdAndStatus(Long doctorId, String status);

    long countByDoctorIdAndStatusIn(Long doctorId, List<String> statuses);
}

