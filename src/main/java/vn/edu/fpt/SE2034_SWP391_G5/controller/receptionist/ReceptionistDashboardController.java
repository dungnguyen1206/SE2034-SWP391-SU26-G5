package vn.edu.fpt.SE2034_SWP391_G5.controller.receptionist;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class ReceptionistDashboardController {

    private final ReceptionistService receptionistService;

    @GetMapping("/receptionist/dashboard")
    public String showDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam(required = false) String search, Model model) {
        model.addAttribute("receptionist",
                receptionistService.getReceptionistByUsername(userDetails.getUser().getEmail()));

        model.addAttribute("stats",
                receptionistService.getTodayDashboardStatistics());

        model.addAttribute("todayAppointments",
                receptionistService.getTodayAppointmentsForDashboard(search));

        model.addAttribute("search", search);
        model.addAttribute("todayText", getTodayText());

        return "receptionist/dashboard";
    }

    private String getTodayText() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
