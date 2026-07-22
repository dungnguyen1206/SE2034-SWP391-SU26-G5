package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateAppointmentRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AppointmentService {
    long getAllAppointment();

    Map<String, Long> findTodayAppointmentsByStatus(LocalDate localDate);

    Page<AppointmentResponse> findAppointmentsByBookingDate(LocalDate today, Integer pageNumber, Integer pageSize);

    // ======================== LIST APPOINTMENT RECEPTIONIST ========================

    Page<AppointmentResponse> searchAppointmentListForReceptionist(String search, String status, LocalDate fromDate,
            LocalDate toDate, int page, int size);

    Map<String, Long> getAppointmentStatusCountsInDateRangeForReceptionist(LocalDate fromDate, LocalDate toDate);

    List<AppointmentDateGroupResponse> groupAppointmentsByDate(List<AppointmentResponse> appointments);

    // ======================== END LIST APPOINTMENT RECEPTIONIST
    // ========================

    // ======================== VIEW DETAIL APPOINTMENT RECEPTIONIST
    // ========================
    AppointmentResponse getAppointmentDetailForReceptionist(Long appointmentId);
    // ======================== END VIEW DETAIL APPOINTMENT RECEPTIONIST
    // ========================

    // ======================== CHECK-IN RECEPTIONIST ========================
    AppointmentPrintResponse getCheckInTicket(Long appointmentId);

    void confirmCheckInAppointment(Long appointmentId);

    AppointmentPrintResponse confirmCheckInByCode(String appointmentCode);

    AppointmentPrintResponse confirmCheckInByPhone(String phone);

    Long calculateQueueNumber(vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment appointment);
    // ======================== END CHECK-IN RECEPTIONIST ========================

    // ======================== QUEUE BOARD RECEPTIONIST ========================
    List<QueueResponse> getTodayQueueBoard();
    // ======================== END QUEUE BOARD RECEPTIONIST
    // ========================

    // Lấy lịch + slot của bác sĩ để hiển thị trên form đặt lịch
    List<ScheduleSlotResponse> getAvailableSchedules(Long doctorId);

    // Đặt lịch
    AppointmentResponse bookAppointment(Long patientId, CreateAppointmentRequest request);

    // Lấy danh sách lịch hẹn của bệnh nhân
    List<AppointmentResponse> getAppointmentsByPatient(Long patientId);

    // Lấy chi tiết 1 lịch hẹn (kiểm tra ownership)
    AppointmentResponse getAppointmentDetail(Long appointmentId, Long patientId);

    // Hủy lịch hẹn
    void cancelAppointment(Long appointmentId, Long patientId);

    // LinhNH
    // Lấy danh sách lịch hẹn của bác sĩ có phân trang và bộ lọc trạng thái theo
    // ngày
    Page<AppointmentResponse> getAppointmentsForDoctor(Long doctorId, LocalDate bookingDate, String status,
            Pageable pageable);

    // LinhNH
    // Đếm số lượng lịch hẹn của bác sĩ theo trạng thái theo ngày
    long countAppointmentsForDoctor(Long doctorId, LocalDate bookingDate, String status);

    // LinhNH
    // Cập nhật trạng thái lịch hẹn
    void updateAppointmentStatus(Long appointmentId, String newStatus);

    // LinhNH
    // Lấy danh sách bệnh nhân đã khám xong gần đây nhất
    List<AppointmentResponse> getRecentCompletedAppointmentsForDoctor(Long doctorId, int limit);

}