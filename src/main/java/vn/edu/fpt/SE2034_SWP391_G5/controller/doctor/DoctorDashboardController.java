package vn.edu.fpt.SE2034_SWP391_G5.controller.doctor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;

@Controller
@RequestMapping("/doctor")
public class DoctorDashboardController {

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        return "doctor/dashboard";
    }
}

