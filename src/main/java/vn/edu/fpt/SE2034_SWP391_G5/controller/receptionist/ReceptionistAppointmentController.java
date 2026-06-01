package vn.edu.fpt.SE2034_SWP391_G5.controller.receptionist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentPrintResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReceptionistAppointmentController {

    private final AppointmentService appointmentService;
    private final ReceptionistService receptionistService;

    @GetMapping("/receptionist/appointment")
    public String showAppointmentList(@RequestParam(required = false) String search, @RequestParam(required = false) String status, Model model) {
        List<AppointmentResponse> appointments = getFilteredAppointments(search, status);
        model.addAttribute("appointments", appointments);

        addAppointmentCounts(model, appointments);
        addReceptionistInfo(model);
        addPageInfo(model, search, status);
        
        return "receptionist/appointment/list";
    }

    private List<AppointmentResponse> getFilteredAppointments(String search, String status){
        List<AppointmentResponse> allAppointments = appointmentService.getAppointmentListForReceptionist();
        return appointmentService.filterAppointments(allAppointments, search, status);
    }

    private void addAppointmentCounts(Model model, List<AppointmentResponse> appointments){
        model.addAttribute("confirmedCount", appointmentService.countByStatus(appointments, "CONFIRMED"));
        model.addAttribute("waitingCount", appointmentService.countByStatus(appointments, "WAITING"));
        model.addAttribute("examiningCount", appointmentService.countByStatus(appointments, "EXAMINING"));
        model.addAttribute("completedCount", appointmentService.countByStatus(appointments, "COMPLETED"));
        model.addAttribute("cancelledCount", appointmentService.countByStatus(appointments, "CANCELLED"));
        model.addAttribute("noShowCount", appointmentService.countByStatus(appointments, "NO_SHOW"));
    }

    private void addReceptionistInfo(Model model){
        model.addAttribute("receptionist", receptionistService.getReceptionistByUsername("recept.linh"));
    }

    private void addPageInfo(Model model, String search, String status){
        model.addAttribute("search", search);
        model.addAttribute("selectStatus", status);
        model.addAttribute("currentDateTime", getCurrentDateTime());
    }

    private String getCurrentDateTime(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm"));
    }

    @GetMapping("/receptionist/appointment/{id}/check-in-ticket")
    public String showCheckInTicket(
            @PathVariable Long id,
            Model model
    ) {
        AppointmentPrintResponse ticket =
                appointmentService.getCheckInTicket(id);

        boolean checkedIn = ticket.getCheckInTime() != null;

        boolean canCheckIn =
                !checkedIn
                        && "CONFIRMED".equals(ticket.getStatus())
                        && LocalDate.now().equals(ticket.getBookingDate());

        model.addAttribute("ticket", ticket);
        model.addAttribute("checkedIn", checkedIn);
        model.addAttribute("canCheckIn", canCheckIn);

        return "receptionist/appointment/check-in-ticket";
    }

    @PostMapping("/receptionist/appointment/{id}/confirm-check-in")
    public String confirmCheckIn(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
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