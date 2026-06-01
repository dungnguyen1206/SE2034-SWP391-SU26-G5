package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentPrintResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;

import java.util.List;

public interface AppointmentService {

    List<AppointmentResponse> getAppointmentListForReceptionist();

    long countByStatus(List<AppointmentResponse> appointments, String status);

    List<AppointmentResponse> filterAppointments(List<AppointmentResponse> appointments,String search, String status);

    AppointmentPrintResponse getCheckInTicket(Long appointmentId);

    void confirmCheckInAppointment(Long appointmentId);
}