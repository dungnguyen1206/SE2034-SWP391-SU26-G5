package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentStatusCountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorOnDutyResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.service.*;
import vn.edu.fpt.SE2034_SWP391_G5.service.impl.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manager/dashboard")
public class ManagerDashboardController {

    private PatientService  patientService;
    private AppointmentService appointmentService;
    private DoctorService doctorService;
    private InvoiceService invoiceService;
    private ScheduleService scheduleService;

    public ManagerDashboardController(PatientService patientService,AppointmentService appointmentService,DoctorService doctorService,InvoiceService invoiceService,ScheduleService scheduleService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.invoiceService = invoiceService;
        this.scheduleService = scheduleService;
    }
    @GetMapping
    public String dashboard(Model model) {

        long totalPatient= patientService.findUsersByRoleName("PATIENT").size();
        long totalAppointment = appointmentService.getAllAppointment();
        long doctorActive = doctorService.findByDoctorStatus("ACTIVE").size();
        long doctorInactive = doctorService.findByDoctorStatus("INACTIVE").size();
        BigDecimal totalAmount = invoiceService.getTotalAmount("PAID", LocalDate.now().getMonthValue(),LocalDate.now().getYear());
        Map<String,Long> appointmentStatusCountResponseMap = appointmentService.findTodayAppointmentsByStatus(LocalDate.now());
        List<AppointmentResponse> todayAppointmentsList = appointmentService.findAppointmentsByBookingDate(LocalDate.now());
        List<DoctorOnDutyResponse> doctorOnDutyResponses = scheduleService.findDoctorScheduleByDate(LocalDate.now());

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
