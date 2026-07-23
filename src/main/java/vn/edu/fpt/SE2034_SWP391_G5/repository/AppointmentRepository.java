package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentStatusCountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistDashboardResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // ======================== DASHBOARD RECEPTIONIST ========================
    // Đếm tổng lịch hẹn trong ngày hiện tại.
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.bookingDate = :today")
    long countTodayAppointments(@Param("today") LocalDate today);

    // Đếm số lịch hẹn đã check-in trong ngày hiện tại.
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.bookingDate = :today AND a.checkInTime IS NOT NULL")
    long countTodayCheckedInAppointments(@Param("today") LocalDate today);

    // Đếm số bệnh nhân đang chờ khám trong ngày hiện tại.
    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.bookingDate = :today " +
            "AND a.status = 'WAITING'")
    long countTodayWaitingAppointments(@Param("today") LocalDate today);

    // Lấy tổng số lượng theo từng trạng thái bằng 1 câu truy vấn duy nhất
    @Query("SELECT new vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentStatusCountResponse(a.status, COUNT(a)) " +
           "FROM Appointment a " +
           "WHERE a.bookingDate BETWEEN :fromDate AND :toDate " +
           "GROUP BY a.status")
    List<AppointmentStatusCountResponse> countAppointmentsByStatusInDateRangeGroupByStatus(
            @Param("fromDate") LocalDate fromDate, 
            @Param("toDate") LocalDate toDate
    );

    @Query("SELECT new vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistDashboardResponse(" +
            "COUNT(a), " +
            "COALESCE(SUM(CASE WHEN a.checkInTime IS NOT NULL THEN 1L ELSE 0L END), 0L), " +
            "COALESCE(SUM(CASE WHEN a.status = 'WAITING' THEN 1L ELSE 0L END), 0L), " +
            "COALESCE(SUM(CASE WHEN a.status = 'EXAMINING' THEN 1L ELSE 0L END), 0L), " +
            "0L, 0L) " +
            "FROM Appointment a WHERE a.bookingDate = :today")
    ReceptionistDashboardResponse getTodayAppointmentDashboardCounts(
            @Param("today") LocalDate today
    );

    // Lấy danh sách lịch hẹn hôm nay trên Dashboard.
    // Có hỗ trợ tìm kiếm theo họ, tên đệm, tên hoặc số điện thoại bệnh nhân.
    // Chỉ query lịch hẹn của ngày hiện tại, không lấy toàn bộ danh sách.
    @Query(
            value = "SELECT a FROM Appointment a " +
            "JOIN FETCH a.patient p " +
            "JOIN FETCH a.doctor d " +
            "JOIN FETCH a.service s " +
            "JOIN FETCH s.department dep " +
            "JOIN FETCH a.slot sl " +
            "JOIN FETCH sl.schedule ds " +
            "JOIN FETCH ds.room r " +
            "WHERE a.bookingDate = :today " +
            "AND (:search IS NULL OR :search = '' " +
            "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(CONCAT(p.lastName, ' ', p.firstName)) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(CONCAT(p.lastName, ' ', p.middleName, ' ', p.firstName)) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR p.phone LIKE CONCAT('%', :search, '%')) " +
            "ORDER BY sl.startTime DESC, a.createdAt DESC",
            countQuery = "SELECT COUNT(a) FROM Appointment a " +
            "JOIN a.patient p " +
            "WHERE a.bookingDate = :today " +
            "AND (:search IS NULL OR :search = '' " +
            "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(CONCAT(p.lastName, ' ', p.firstName)) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(CONCAT(p.lastName, ' ', p.middleName, ' ', p.firstName)) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR p.phone LIKE CONCAT('%', :search, '%'))"
    )
    Page<Appointment> findTodayAppointmentsForDashboard(
            @Param("today") LocalDate today,
            @Param("search") String search,
            Pageable pageable
    );
    // ======================== END DASHBOARD RECEPTIONIST ========================

    // ======================== LIST APPOINTMENT RECEPTIONIST ========================
    // Lấy danh sách lịch hẹn theo ngày hiển thị lên màn hình Appointment List of Receptionist
    @Query(
            value = "SELECT a FROM Appointment a " +
                    "JOIN FETCH a.patient p " +
                    "JOIN FETCH a.doctor d " +
                    "JOIN FETCH a.service s " +
                    "JOIN FETCH s.department dep " +
                    "JOIN FETCH a.slot sl " +
                    "JOIN FETCH sl.schedule sch " +
                    "JOIN FETCH sch.room r " +
                    "WHERE a.bookingDate BETWEEN :fromDate AND :toDate " +
                    "ORDER BY a.bookingDate DESC, sl.startTime DESC, a.createdAt DESC",
            countQuery = "SELECT COUNT(a) FROM Appointment a " +
                    "WHERE a.bookingDate BETWEEN :fromDate AND :toDate"
    )
    Page<Appointment> findAppointmentListForReceptionistList(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, Pageable pageable);

    // Tìm kiếm, lọc trạng thái, lọc ngày lịch hẹn
    @Query(
            value = "SELECT a FROM Appointment a " +
                    "JOIN FETCH a.patient p " +
                    "JOIN FETCH a.doctor d " +
                    "JOIN FETCH a.service s " +
                    "JOIN FETCH s.department dep " +
                    "JOIN FETCH a.slot sl " +
                    "JOIN FETCH sl.schedule ds " +
                    "JOIN FETCH ds.room r " +
                    "WHERE a.bookingDate BETWEEN :fromDate AND :toDate " +
                    "AND (:status IS NULL OR :status = '' OR a.status = :status) " +
                    "AND (:search IS NULL OR :search = '' " +
                    "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(CONCAT(p.lastName, ' ', p.firstName)) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(CONCAT(p.lastName, ' ', p.middleName, ' ', p.firstName)) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR p.phone LIKE CONCAT('%', :search, '%')) " +
                    "ORDER BY a.bookingDate DESC, sl.startTime DESC, a.createdAt DESC",
            countQuery = "SELECT COUNT(a) FROM Appointment a " +
                    "JOIN a.patient p " +
                    "WHERE a.bookingDate BETWEEN :fromDate AND :toDate " +
                    "AND (:status IS NULL OR :status = '' OR a.status = :status) " +
                    "AND (:search IS NULL OR :search = '' " +
                    "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(CONCAT(p.lastName, ' ', p.firstName)) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(CONCAT(p.lastName, ' ', p.middleName, ' ', p.firstName)) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR p.phone LIKE CONCAT('%', :search, '%'))"
    )
    Page<Appointment> searchAppointmentListForReceptionist(@Param("search") String search, @Param("status") String status, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.bookingDate BETWEEN :fromDate AND :toDate AND a.status = 'CONFIRMED'")
    long countConfirmedAppointmentsInDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.bookingDate BETWEEN :fromDate AND :toDate AND a.status = 'WAITING'")
    long countWaitingAppointmentsInDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.bookingDate BETWEEN :fromDate AND :toDate AND a.status = 'EXAMINING'")
    long countExaminingAppointmentsInDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.bookingDate BETWEEN :fromDate AND :toDate AND a.status = 'COMPLETED'")
    long countCompletedAppointmentsInDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.bookingDate BETWEEN :fromDate AND :toDate AND a.status = 'CANCELLED'")
    long countCancelledAppointmentsInDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.bookingDate BETWEEN :fromDate AND :toDate AND a.status = 'NO_SHOW'")
    long countNoShowAppointmentsInDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    // ======================== END LIST APPOINTMENT RECEPTIONIST ========================

    // ======================== VIEW DETAIL APPOINTMENT RECEPTIONIST ========================
    @Query("SELECT DISTINCT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient p " +
            "LEFT JOIN FETCH p.addresses addr " +
            "LEFT JOIN FETCH addr.province " +
            "LEFT JOIN FETCH a.doctor " +
            "LEFT JOIN FETCH a.service sv " +
            "LEFT JOIN FETCH sv.department " +
            "LEFT JOIN FETCH a.slot sl " +
            "LEFT JOIN FETCH sl.schedule sch " +
            "LEFT JOIN FETCH sch.room " +
            "WHERE a.id = :appointmentId")
    Optional<Appointment> findAppointmentDetailById(@Param("appointmentId") Long appointmentId);
    // ======================== END VIEW DETAIL APPOINTMENT RECEPTIONIST ========================

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
    Page<Appointment> findAppointmentsByBookingDate(@Param("today") LocalDate today, Pageable pageable);



    // ======================== CHECK-IN RECEPTIONIST ========================
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
    // ======================== END CHECK-IN RECEPTIONIST ========================

    // ======================== WALK-IN BOOKING RECEPTIONIST ========================
    boolean existsBySlotIdAndPatientIdAndStatusNotIn(Long slotId, Long patientId, List<String> excludedStatuses);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a " +
           "WHERE a.patient.id = :patientId " +
           "AND a.bookingDate = :bookingDate " +
           "AND a.service.department.id = :departmentId " +
           "AND a.status NOT IN :excludedStatuses")
    boolean existsByPatientIdAndDepartmentIdAndBookingDateAndStatusNotIn(
            @Param("patientId") Long patientId, 
            @Param("departmentId") Integer departmentId, 
            @Param("bookingDate") LocalDate bookingDate, 
            @Param("excludedStatuses") List<String> excludedStatuses);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a " +
           "WHERE a.patient.id = :patientId " +
           "AND a.bookingDate = :bookingDate " +
           "AND a.status NOT IN :excludedStatuses")
    boolean existsByPatientIdAndBookingDateAndStatusNotIn(
            @Param("patientId") Long patientId, 
            @Param("bookingDate") LocalDate bookingDate, 
            @Param("excludedStatuses") List<String> excludedStatuses);
    // ======================== END WALK-IN BOOKING RECEPTIONIST ========================
    // ======================== QUEUE BOARD RECEPTIONIST ========================
    @Query("SELECT a FROM Appointment a " +
            "JOIN FETCH a.patient p " +
            "JOIN FETCH a.doctor d " +
            "JOIN FETCH a.service s " +
            "JOIN FETCH s.department dep " +
            "JOIN FETCH a.slot sl " +
            "JOIN FETCH sl.schedule sch " +
            "JOIN FETCH sch.room r " +
            "WHERE a.bookingDate = :today " +
            "AND a.status IN ('WAITING', 'EXAMINING') " +
            "ORDER BY r.roomNumber ASC, a.checkInTime ASC, a.id ASC")
    List<Appointment> findQueueAppointmentsToday(@Param("today") LocalDate today);
    // ======================== END QUEUE BOARD RECEPTIONIST ========================

    // ======================== LIST INVOICE RECEPTIONIST ========================
    @Query(
            value = "SELECT DISTINCT a FROM Appointment a " +
                    "LEFT JOIN FETCH a.patient p " +
                    "WHERE a.bookingDate = CURRENT_DATE " +
                    "AND a.status != 'CANCELLED' " +
                    "AND (:search IS NULL OR :search = '' " +
                    "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(CONCAT(p.lastName, ' ', p.firstName)) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(CONCAT(p.lastName, ' ', p.middleName, ' ', p.firstName)) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR p.phone LIKE CONCAT('%', :search, '%')) " +
                    "ORDER BY a.createdAt DESC",
            countQuery = "SELECT COUNT(DISTINCT a) FROM Appointment a " +
                    "JOIN a.patient p " +
                    "WHERE a.bookingDate = CURRENT_DATE " +
                    "AND a.status != 'CANCELLED' " +
                    "AND (:search IS NULL OR :search = '' " +
                    "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(CONCAT(p.lastName, ' ', p.firstName)) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(CONCAT(p.lastName, ' ', p.middleName, ' ', p.firstName)) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR p.phone LIKE CONCAT('%', :search, '%'))"
    )
    Page<Appointment> findAppointmentsForBilling(@Param("search") String search, Pageable pageable);
    
    @Query(
            value = "SELECT DISTINCT a FROM Appointment a " +
                    "LEFT JOIN FETCH a.patient p " +
                    "LEFT JOIN FETCH a.service s " +
                    "LEFT JOIN FETCH a.medicalRecord mr " +
                    "LEFT JOIN FETCH a.invoices inv " +
                    "WHERE a.bookingDate = CURRENT_DATE " +
                    "AND a.status != 'CANCELLED' " +
                    "AND (:search IS NULL OR :search = '' " +
                    "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(CONCAT(p.lastName, ' ', p.firstName)) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(CONCAT(p.lastName, ' ', p.middleName, ' ', p.firstName)) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR p.phone LIKE CONCAT('%', :search, '%')) " +
                    "ORDER BY a.createdAt DESC"
    )
    List<Appointment> findAllAppointmentsForBilling(@Param("search") String search);
    // ======================== END LIST INVOICE RECEPTIONIST ========================

    // ------------------------------------------------------------------------------------

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



    boolean existsByPatientIdAndStatusIn(Long patientId, List<String> statuses);

    @Query(value = "SELECT CASE WHEN COUNT_BIG(a.id) > 0 THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END " +
            "FROM appointments a " +
            "JOIN time_slots ts ON ts.id = a.slot_id " +
            "WHERE a.patient_id = :patientId " +
            "AND a.status IN (:activeStatuses) " +
            "AND (a.booking_date < :targetDate " +
            "     OR (a.booking_date = :targetDate " +
            "         AND ts.start_time < CAST(:targetEndTime AS TIME)))",
            nativeQuery = true)
    boolean existsActiveAppointmentBefore(
            @Param("patientId") Long patientId,
            @Param("targetDate") LocalDate targetDate,
            @Param("targetEndTime") LocalTime targetEndTime,
            @Param("activeStatuses") List<String> activeStatuses);

    boolean existsByPatientIdAndBookingDateAndStatusIn(Long patientId, LocalDate bookingDate, List<String> statuses);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId AND a.status = :status")
    long countByPatientIdAndStatus(@Param("patientId") Long patientId, @Param("status") String status);


    //LinhNH 1/6/2026

    Page<Appointment> findByDoctorIdAndBookingDateAndStatusIn(Long doctorId, LocalDate bookingDate, List<String> statuses, Pageable pageable);

    long countByDoctorIdAndBookingDateAndStatus(Long doctorId, LocalDate bookingDate, String status);

    long countByDoctorIdAndBookingDateAndStatusIn(Long doctorId, LocalDate bookingDate, List<String> statuses);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.bookingDate = :bookingDate")
    long countByDoctorIdAndBookingDate(@Param("doctorId") Long doctorId, @Param("bookingDate") LocalDate bookingDate);


    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient " +
            "LEFT JOIN FETCH a.slot sl " +
            "WHERE a.doctor.id = :doctorId AND a.status = :status " +
            "ORDER BY a.bookingDate DESC, a.id DESC")
    List<Appointment> findRecentCompletedAppointments(@Param("doctorId") Long doctorId,
                                                      @Param("status") String status,
                                                      Pageable pageable);


    @Query("SELECT a FROM Appointment a WHERE a.slot.schedule.id = :scheduleId AND a.status IN ('WAITING', 'CONFIRMED')")
    List<Appointment> findActiveAppointmentsByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("SELECT a FROM Appointment a WHERE a.slot.schedule.id = :scheduleId AND a.status IN ('WAITING', 'CONFIRMED')")
    List<Appointment> findPendingAppointmentsByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("SELECT a FROM Appointment a " +
            "JOIN FETCH a.slot sl " +
            "WHERE a.bookingDate = :bookingDate " +
            "AND a.status = 'CONFIRMED'")
    List<Appointment> findConfirmedAppointmentsByBookingDate(@Param("bookingDate") LocalDate bookingDate);


}
