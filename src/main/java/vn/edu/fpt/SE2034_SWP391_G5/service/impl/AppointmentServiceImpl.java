package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.repository.AppointmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;

import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private AppointmentRepository appointmentRepository;
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }
    public long getAllAppointment(){
        return appointmentRepository.count();
    };

    public List<Appointment> findAppointmentsByStatus(String status){
        return appointmentRepository.findAppointmentsByStatus(status);
    };

    // Find all today's appointment
    public List<AppointmentResponse> findAppointmentsByBookingDate(int today){
       List<Appointment> todayListAppointment = appointmentRepository.findAppointmentsByBookingDate(today);
       List<AppointmentResponse> todayListAppointmentResponse = new ArrayList<>();
       todayListAppointment.forEach(a -> {
          AppointmentResponse appointmentResponse = new AppointmentResponse();
          appointmentResponse.setAppointmentCode(a.getAppointmentCode());
          appointmentResponse.setPatientName(a.getPatient().getFirstName() + " "+a.getPatient().getMiddleName()+" " +  a.getPatient().getLastName());
          appointmentResponse.setDoctorName(a.getDoctor().getFirstName() + " "+a.getDoctor().getMiddleName()+" " +  a.getDoctor().getLastName());
          appointmentResponse.setServiceName(a.getService().getName());
          appointmentResponse.setStartTime(a.getSlot().getStartTime());
          appointmentResponse.setEndTime(a.getSlot().getEndTime());
          appointmentResponse.setStatus(a.getStatus());
          todayListAppointmentResponse.add(appointmentResponse);
       });
       return todayListAppointmentResponse;
    };
}
