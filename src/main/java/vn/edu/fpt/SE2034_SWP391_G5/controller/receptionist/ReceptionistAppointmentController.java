package vn.edu.fpt.SE2034_SWP391_G5.controller.receptionist;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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
    public String showAppointmentList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        LocalDate today = LocalDate.now();
        if(fromDate == null){
            fromDate = today.minusDays(7);
        }
        if(toDate == null){
            toDate = today;
        }
        if(page < 0){
            page = 0;
        }
        int size = 20;

        Page<AppointmentResponse> appointmentPage = appointmentService.getPagedAppointmentsForReceptionist(search, status, fromDate, toDate, page, size);
        List<AppointmentResponse> appointments = appointmentPage.getContent();

        model.addAttribute("appointments", appointments);
        model.addAttribute("appointmentPage", appointmentPage);
        model.addAttribute("appointmentGroups", appointmentService.groupAppointmentsByDate(appointments));

        addAppointmentCounts(model, appointments);
        addReceptionistInfo(model);
        addPageInfo(model, search, status, fromDate, toDate);
        
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
        model.addAttribute("receptionist", receptionistService.getReceptionistByUsername("recept.linh@hams.vn"));
    }

    private void addPageInfo(Model model, String search, String status, LocalDate fromDate, LocalDate toDate) {
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("currentDateTime", getCurrentDateTime());
    }

    private String getCurrentDateTime(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm"));
    }

    @GetMapping("/receptionist/appointment/{id}")
    public String showAppointmentDetail(@PathVariable Long id, Model model){
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(id);
        model.addAttribute("appointment", appointment);
        addReceptionistInfo(model);
        model.addAttribute("currentDateTime", getCurrentDateTime());
        return "receptionist/appointment/detail";
    }
}