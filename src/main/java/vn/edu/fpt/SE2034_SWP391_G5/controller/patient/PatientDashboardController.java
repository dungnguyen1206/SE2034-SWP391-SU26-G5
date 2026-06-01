package vn.edu.fpt.SE2034_SWP391_G5.controller.patient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.PatientResponse;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.PatientService;

import java.util.List;

@Controller
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientDashboardController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    // TODO: thay bằng @AuthenticationPrincipal khi auth sẵn sàng
    private static final Long DEMO_PATIENT_ID = 14L;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        PatientResponse profile = patientService.getProfile(DEMO_PATIENT_ID);
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsByPatient(DEMO_PATIENT_ID);

        long totalAppointments = appointments.size();
        long pendingCount   = appointments.stream().filter(a -> "WAITING".equals(a.getStatus())).count();
        long confirmedCount = appointments.stream().filter(a -> "CONFIRMED".equals(a.getStatus())).count();
        long completedCount = appointments.stream().filter(a -> "COMPLETED".equals(a.getStatus())).count();
        List<AppointmentResponse> recentAppointments = appointments.stream().limit(5).toList();

        model.addAttribute("profile", profile);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("confirmedCount", confirmedCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("recentAppointments", recentAppointments);

        return "patient/dashboard";
    }
}
