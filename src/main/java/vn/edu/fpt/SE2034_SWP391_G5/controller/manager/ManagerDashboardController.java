package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentStatusCountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorOnDutyResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.service.PatientService;
import vn.edu.fpt.SE2034_SWP391_G5.service.impl.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manager/dashboard")
public class ManagerDashboardController {

    private PatientServiceImpl  patientServiceImpl;
    private AppointmentServiceImpl appointmentServiceImpl;
    private DoctorServiceImpl doctorServiceImpl;
    private InvoiceServiceImpl invoiceServiceImpl;
    private ScheduleServiceImpl scheduleServiceImpl;
    public ManagerDashboardController(PatientServiceImpl patientServiceImpl, AppointmentServiceImpl appointmentServiceImpl, DoctorServiceImpl doctorServiceImpl,
        InvoiceServiceImpl invoiceServiceImpl, ScheduleServiceImpl scheduleServiceImpl
    ) {
        this.patientServiceImpl = patientServiceImpl;
        this.appointmentServiceImpl = appointmentServiceImpl;
        this.doctorServiceImpl = doctorServiceImpl;
        this.invoiceServiceImpl = invoiceServiceImpl;
        this.scheduleServiceImpl = scheduleServiceImpl;
    }
    @GetMapping
    public String dashboard(Model model) {

        long totalPatient= patientServiceImpl.findUsersByRoleName("PATIENT").size();
        long totalAppointment = appointmentServiceImpl.getAllAppointment();
        long doctorActive = doctorServiceImpl.findByDoctorStatus("ACTIVE").size();
        long doctorInactive = doctorServiceImpl.findByDoctorStatus("INACTIVE").size();
        BigDecimal totalAmount = invoiceServiceImpl.getTotalAmount("PAID", LocalDate.now().getMonthValue(),LocalDate.now().getYear());
        Map<String,Long> appointmentStatusCountResponseMap = appointmentServiceImpl.findTodayAppointmentsByStatus(LocalDate.now());
        List<AppointmentResponse> todayAppointmentsList = appointmentServiceImpl.findAppointmentsByBookingDate(LocalDate.now());
        List<DoctorOnDutyResponse> doctorOnDutyResponses = scheduleServiceImpl.findDoctorScheduleByDate(LocalDate.now());

        model.addAttribute("totalPatient", totalPatient);
        model.addAttribute("totalAppointment", totalAppointment);
        model.addAttribute("doctorActive", doctorActive);
        model.addAttribute("doctorInactive", doctorInactive);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("waitingAppointment", appointmentStatusCountResponseMap.get("WAITING"));
        model.addAttribute("confirmedAppointment", appointmentStatusCountResponseMap.get("CONFIRMED"));
        model.addAttribute("examiningAppointment", appointmentStatusCountResponseMap.get("EXAMINING"));
        model.addAttribute("completedAppointment", appointmentStatusCountResponseMap.get("COMPLETED"));
        model.addAttribute("cancelledAppointment", appointmentStatusCountResponseMap.get("CANCELLED"));
        model.addAttribute("todayAppointmentsList", todayAppointmentsList);
        model.addAttribute("doctorOnDutyResponses", doctorOnDutyResponses);
        return "manager/dashboard";
    }
}
