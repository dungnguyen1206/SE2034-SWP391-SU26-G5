package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateAppointmentRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentPrintResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleSlotResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;

import java.util.List;

public interface AppointmentService {

    List<AppointmentResponse> getAppointmentListForReceptionist();

    long countByStatus(List<AppointmentResponse> appointments, String status);

    List<AppointmentResponse> filterAppointments(List<AppointmentResponse> appointments,String search, String status);

    AppointmentPrintResponse getCheckInTicket(Long appointmentId);

    void confirmCheckInAppointment(Long appointmentId);

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