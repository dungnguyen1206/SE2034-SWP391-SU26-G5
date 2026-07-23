package vn.edu.fpt.SE2034_SWP391_G5.controller.kiosk;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentPrintResponse;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;

@Controller
@RequestMapping("/kiosk")
@RequiredArgsConstructor
public class KioskController {

    private final AppointmentService appointmentService;

    // ========== Online Check-in (có mã lịch hẹn) ==========
    @GetMapping("/check-in")
    public String showCheckInPage() {
        return "kiosk/check-in";
    }

    @PostMapping("/check-in")
    public String processCheckIn(@RequestParam String phone,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        try {
            AppointmentPrintResponse ticket = appointmentService.confirmCheckInByPhone(phone);
            model.addAttribute("ticket", ticket);
            return "kiosk/ticket-result";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/kiosk/check-in";
        }
    }


}
