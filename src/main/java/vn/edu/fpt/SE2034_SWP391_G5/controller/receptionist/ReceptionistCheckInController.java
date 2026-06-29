package vn.edu.fpt.SE2034_SWP391_G5.controller.receptionist;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentPrintResponse;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;

import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class ReceptionistCheckInController {

    private final AppointmentService appointmentService;
    private final ReceptionistService receptionistService;

    @GetMapping("/receptionist/appointment/{id}/check-in-ticket")
    public String showCheckInTicket(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        AppointmentPrintResponse ticket = appointmentService.getCheckInTicket(id);

        boolean checkedIn = ticket.getCheckInTime() != null;
        boolean isToday = ticket.getBookingDate() != null && ticket.getBookingDate().isEqual(LocalDate.now());
        boolean canCheckIn = !checkedIn && isToday && "CONFIRMED".equalsIgnoreCase(ticket.getStatus());

        model.addAttribute("ticket", ticket);
        model.addAttribute("checkedIn", checkedIn);
        model.addAttribute("canCheckIn", canCheckIn);
        model.addAttribute("receptionist", receptionistService.getReceptionistByUsername(userDetails.getUser().getEmail()));

        return "receptionist/appointment/check-in-ticket";
    }

    @PostMapping("/receptionist/appointment/{id}/confirm-check-in")
    public String confirmCheckIn(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.confirmCheckInAppointment(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Check-in thành công. Bạn có thể in phiếu khám."
            );
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }
        return "redirect:/receptionist/appointment/" + id + "/check-in-ticket";
    }
}


