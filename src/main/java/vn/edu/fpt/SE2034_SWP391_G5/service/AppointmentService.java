package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.repository.query.Param;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentStatusCountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateAppointmentRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleSlotResponse;


public interface AppointmentService {
    long getAllAppointment();
    Map<String, Long> findTodayAppointmentsByStatus(LocalDate localDate);
    List<AppointmentResponse> findAppointmentsByBookingDate(LocalDate today);

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
}
