package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateAppointmentRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentDateGroupResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentPrintResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleSlotResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AppointmentService {
    long getAllAppointment();

    Map<String, Long> findTodayAppointmentsByStatus(LocalDate localDate);

    List<AppointmentResponse> findAppointmentsByBookingDate(LocalDate today);

    //------------------------------ Hoàng Linh ---------------------------------------------------------------
    // Lấy danh sách lịch hẹn để hiển thị lên màn hình Appointment List
    Page<AppointmentResponse> getAppointmentListForReceptionist(LocalDate fromDate, LocalDate toDate, int page, int size);

    Page<AppointmentResponse> searchAppointmentListForReceptionist(String search, String status, LocalDate fromDate, LocalDate toDate, int page, int size);

    Map<String, Long> getAppointmentStatusCountsInDateRangeForReceptionist(LocalDate fromDate, LocalDate toDate);

    AppointmentPrintResponse getCheckInTicket(Long appointmentId);

    void confirmCheckInAppointment(Long appointmentId);

    AppointmentResponse getAppointmentDetailForReceptionist(Long appointmentId);

    List<AppointmentDateGroupResponse> groupAppointmentsByDate(List<AppointmentResponse> appointments);
    //------------------------------------------------------------------------------------------------------------

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

    //LinhNH 01/06/2026
    // Lấy danh sách lịch hẹn của bác sĩ có phân trang và bộ lọc trạng thái theo ngày
    Page<AppointmentResponse> getAppointmentsForDoctor(Long doctorId, LocalDate bookingDate, String status, Pageable pageable);

    // Đếm số lượng lịch hẹn của bác sĩ theo trạng thái theo ngày
    long countAppointmentsForDoctor(Long doctorId, LocalDate bookingDate, String status);

    // Cập nhật trạng thái lịch hẹn
    void updateAppointmentStatus(Long appointmentId, String newStatus);

    // Lấy danh sách bệnh nhân đã khám xong gần đây nhất
    List<AppointmentResponse> getRecentCompletedAppointmentsForDoctor(Long doctorId, int limit);
}