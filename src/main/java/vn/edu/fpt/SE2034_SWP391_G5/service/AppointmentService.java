package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentStatusCountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateAppointmentRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentPrintResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleSlotResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;


public interface AppointmentService {
    long getAllAppointment();
    Map<String, Long> findTodayAppointmentsByStatus(LocalDate localDate);
    List<AppointmentResponse> findAppointmentsByBookingDate(LocalDate today);

    List<AppointmentResponse> getAppointmentListForReceptionist();

    long countByStatus(List<AppointmentResponse> appointments, String status);

    List<AppointmentResponse> filterAppointments(List<AppointmentResponse> appointments,String search, String status);

    AppointmentPrintResponse getCheckInTicket(Long appointmentId);

    void confirmCheckInAppointment(Long appointmentId);

    AppointmentResponse getAppointmentDetailForReceptionist(Long appointmentId);

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
    // Lấy danh sách lịch hẹn của bác sĩ có phân trang và bộ lọc trạng thái
    Page<AppointmentResponse> getAppointmentsForDoctor(Long doctorId, String status, Pageable pageable);

    // Đếm số lượng lịch hẹn của bác sĩ theo trạng thái
    long countAppointmentsForDoctor(Long doctorId, String status);

    // Cập nhật trạng thái lịch hẹn
    void updateAppointmentStatus(Long appointmentId, String newStatus);
}