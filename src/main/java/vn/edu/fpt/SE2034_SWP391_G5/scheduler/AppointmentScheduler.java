package vn.edu.fpt.SE2034_SWP391_G5.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.entity.TimeSlot;
import vn.edu.fpt.SE2034_SWP391_G5.repository.AppointmentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class AppointmentScheduler {

    @Autowired
    private AppointmentRepository appointmentRepository;

    // Run every 1 minute
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void markLateAppointmentsAsNoShow() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();

        List<Appointment> confirmedAppointments = appointmentRepository.findConfirmedAppointmentsByBookingDate(today);

        for (Appointment appointment : confirmedAppointments) {
            TimeSlot slot = appointment.getSlot();
            if (slot == null) {
                continue;
            }

            LocalTime lateLimitTime = slot.getEndTime().plusMinutes(30);

            if (currentTime.isAfter(lateLimitTime)) {
                appointment.setStatus("NO_SHOW");
                appointment.setUpdatedAt(now);
                appointmentRepository.save(appointment);
            }
        }
    }
}
