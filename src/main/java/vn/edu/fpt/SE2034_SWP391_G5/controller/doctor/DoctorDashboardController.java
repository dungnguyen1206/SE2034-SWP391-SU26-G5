package vn.edu.fpt.SE2034_SWP391_G5.controller.doctor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/doctor")
public class DoctorDashboardController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "doctor/dashboard";
    }
}

