package vn.edu.fpt.SE2034_SWP391_G5.controller.receptionist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DashboardStatsResponse;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReceptionistDashboardController {

    private final ReceptionistService receptionistService;

    @GetMapping("/receptionist/dashboard")
    public String showDashboard(@RequestParam(required = false) String search, Model model){
        DashboardStatsResponse stats = receptionistService.getDashboardStats();
        List<AppointmentResponse> todayAppointments = receptionistService.getTodayAppointments();
        if(search != null && !search.trim().isEmpty()){
            todayAppointments = receptionistService.searchTodayAppointments(todayAppointments, search);
        }
        model.addAttribute("stats", stats);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("search", search);
        model.addAttribute("todayText", getTodayText());
        model.addAttribute("receptionist", receptionistService.getReceptionistByUsername("recept.linh@hams.vn"));
        model.addAttribute("currentDateTime", getCurrentDateTime());

        return "receptionist/dashboard";
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm"));
    }

    private String getTodayText() {
        return LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
