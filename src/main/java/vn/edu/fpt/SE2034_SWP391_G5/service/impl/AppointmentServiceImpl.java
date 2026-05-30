package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentStatusCountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.repository.AppointmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private AppointmentRepository appointmentRepository;
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }
    public long getAllAppointment(){
        return appointmentRepository.count();
    };

    public Map<String,Long> findTodayAppointmentsByStatus(LocalDate localDate){
        List<AppointmentStatusCountResponse> appointmentStatusCountResponseList = appointmentRepository.findTodayAppointmentsByStatus(localDate);
        Map<String,Long> statusCount = new HashMap<>();
        statusCount.put("WAITING", 0L);
        statusCount.put("CONFIRMED", 0L);
        statusCount.put("EXAMINING", 0L);
        statusCount.put("COMPLETED", 0L);
        statusCount.put("CANCELLED", 0L);
        appointmentStatusCountResponseList.stream().forEach(appointment -> {
            statusCount.put(appointment.getStatus(),appointment.getCount());
        });
            return statusCount;
    };

    // Find all today's appointment
    public List<AppointmentResponse> findAppointmentsByBookingDate(LocalDate today){
       List<Appointment> todayListAppointment = appointmentRepository.findAppointmentsByBookingDate(today);
       List<AppointmentResponse> todayListAppointmentResponse = new ArrayList<>();
       todayListAppointment.forEach(a -> {
          AppointmentResponse appointmentResponse = new AppointmentResponse();
          appointmentResponse.setAppointmentCode(a.getAppointmentCode());
          appointmentResponse.setPatientFullName(a.getPatient().getFirstName() + " "+a.getPatient().getMiddleName()+" " +  a.getPatient().getLastName());
          appointmentResponse.setDoctorFullName(a.getDoctor().getFirstName() + " "+a.getDoctor().getMiddleName()+" " +  a.getDoctor().getLastName());
          appointmentResponse.setServiceName(a.getService().getName());
          appointmentResponse.setSlotStartTime(a.getSlot().getStartTime());
          appointmentResponse.setSlotEndTime(a.getSlot().getEndTime());
          appointmentResponse.setStatus(a.getStatus());
          todayListAppointmentResponse.add(appointmentResponse);
       });
       return todayListAppointmentResponse;
    };
}
