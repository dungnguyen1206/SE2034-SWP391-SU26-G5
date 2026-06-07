package vn.edu.fpt.SE2034_SWP391_G5.controller.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorDashboardController {

    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long doctorId = userDetails.getUser().getId();
        User doctor = userRepository.findById(doctorId).orElse(null);
        if (doctor != null) {
            String doctorName = (doctor.getLastName() != null ? doctor.getLastName() + " " : "")
                    + (doctor.getMiddleName() != null ? doctor.getMiddleName() + " " : "")
                    + (doctor.getFirstName() != null ? doctor.getFirstName() : "");
            model.addAttribute("doctorName", doctorName.trim());
            model.addAttribute("doctorDept", doctor.getDepartment() != null ? doctor.getDepartment().getName() : "");
        } else {
            model.addAttribute("doctorName", "Bác sĩ");
            model.addAttribute("doctorDept", "");
        }
        return "doctor/dashboard";
    }
}

