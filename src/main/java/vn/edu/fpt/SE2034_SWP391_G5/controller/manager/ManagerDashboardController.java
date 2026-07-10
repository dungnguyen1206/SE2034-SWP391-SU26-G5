package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentStatusCountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorOnDutyResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.service.*;
import vn.edu.fpt.SE2034_SWP391_G5.service.impl.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    public String dashboard(Model model,
                            @RequestParam(defaultValue = "10") Integer sizeAppointment,
                            @RequestParam(defaultValue = "0") Integer pageAppointment,
                            @RequestParam(defaultValue = "10") Integer sizeDoctor,
                            @RequestParam(defaultValue = "0") Integer pageDoctor) {

        long totalPatient= patientService.findUsersByRoleName("PATIENT").size();
        long totalAppointment = appointmentService.getAllAppointment();
        long doctorActive = doctorService.findByDoctorStatus("ACTIVE").size();
        long doctorInactive = doctorService.findByDoctorStatus("INACTIVE").size();
        BigDecimal totalAmount = invoiceService.getTotalAmount("PAID", LocalDate.now().getMonthValue(),LocalDate.now().getYear());
        Locale vietnamLocale = new Locale("vi", "VN");
        NumberFormat vnCurrencyFormat = NumberFormat.getCurrencyInstance(vietnamLocale);
        String formattedAmount = vnCurrencyFormat.format(totalAmount);
        Map<String,Long> appointmentStatusCountResponseMap = appointmentService.findTodayAppointmentsByStatus(LocalDate.now());
        Page<AppointmentResponse> todayAppointmentsList = appointmentService.findAppointmentsByBookingDate(LocalDate.now(),pageAppointment,sizeAppointment);
        Page<DoctorOnDutyResponse> doctorOnDutyResponses = scheduleService.findDoctorScheduleByDate(LocalDate.now(),pageDoctor,sizeDoctor);

        model.addAttribute("totalPatient", totalPatient);
        model.addAttribute("totalAppointment", totalAppointment);
        model.addAttribute("doctorActive", doctorActive);
        model.addAttribute("doctorInactive", doctorInactive);
        model.addAttribute("totalAmount", formattedAmount);
        model.addAttribute("waitingAppointment", appointmentStatusCountResponseMap.get("WAITING"));
        model.addAttribute("confirmedAppointment", appointmentStatusCountResponseMap.get("CONFIRMED"));
        model.addAttribute("examiningAppointment", appointmentStatusCountResponseMap.get("EXAMINING"));
        model.addAttribute("completedAppointment", appointmentStatusCountResponseMap.get("COMPLETED"));
        model.addAttribute("cancelledAppointment", appointmentStatusCountResponseMap.get("CANCELLED"));
        model.addAttribute("todayAppointmentsList", todayAppointmentsList.getContent());
        model.addAttribute("doctorOnDutyResponses", doctorOnDutyResponses.getContent());
        model.addAttribute("currentPageAppointment", pageAppointment);
        model.addAttribute("currentPageDoctor", pageDoctor);
        model.addAttribute("totalPageAppointment",todayAppointmentsList.getTotalPages());
        model.addAttribute("totalPageDoctor",doctorOnDutyResponses.getTotalPages());
        model.addAttribute("totalAppointment", todayAppointmentsList.getTotalElements());
        model.addAttribute("totalDoctor", doctorOnDutyResponses.getTotalElements());
        return "manager/dashboard";
    }
}
