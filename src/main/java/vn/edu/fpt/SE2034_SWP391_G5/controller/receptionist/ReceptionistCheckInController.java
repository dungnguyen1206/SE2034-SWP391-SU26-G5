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

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class ReceptionistCheckInController {

    private final AppointmentService appointmentService;

    @GetMapping("/receptionist/appointment/{id}/check-in-ticket")
    public String showCheckInTicket(@PathVariable Long id, Model model) {
        AppointmentPrintResponse ticket = appointmentService.getCheckInTicket(id);

        boolean checkedIn = ticket.getCheckInTime() != null;
        boolean canCheckIn = !checkedIn && "CONFIRMED".equals(ticket.getStatus()) && LocalDate.now().equals(ticket.getBookingDate());

        model.addAttribute("ticket", ticket);
        model.addAttribute("checkedIn", checkedIn);
        model.addAttribute("canCheckIn", canCheckIn);

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


