package vn.edu.fpt.SE2034_SWP391_G5.controller.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorResponse;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;

@ControllerAdvice(basePackages = "vn.edu.fpt.SE2034_SWP391_G5.controller.doctor")
@RequiredArgsConstructor
public class DoctorControllerAdvice {

    private final DoctorService doctorService;

    @ModelAttribute
    public void addDoctorSidebarInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails, 
            Model model) {
        if (userDetails != null && userDetails.getUser() != null) {
            try {
                DoctorResponse doctor = doctorService.getDoctorById(userDetails.getUser().getId());
                model.addAttribute("doctorName", doctor.getFullName() != null ? doctor.getFullName().trim() : "Bác sĩ");
                model.addAttribute("doctorDept", doctor.getDepartmentName() != null ? doctor.getDepartmentName() : "");
                model.addAttribute("doctorAvatar", userDetails.getUser().getAvatar() != null ? userDetails.getUser().getAvatar() : "");
            } catch (Exception e) {
                model.addAttribute("doctorName", "Bác sĩ");
                model.addAttribute("doctorDept", "");
                model.addAttribute("doctorAvatar", "");
            }
        } else {
            model.addAttribute("doctorName", "Bác sĩ");
            model.addAttribute("doctorDept", "");
            model.addAttribute("doctorAvatar", "");
        }
    }
}
