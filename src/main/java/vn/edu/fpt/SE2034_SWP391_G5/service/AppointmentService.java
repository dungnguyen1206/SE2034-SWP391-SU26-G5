package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.repository.query.Param;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;

import java.util.List;

public interface AppointmentService {
    long getAllAppointment();
    List<Appointment> findAppointmentsByStatus(String status);
    List<AppointmentResponse> findAppointmentsByBookingDate(int today);
}
