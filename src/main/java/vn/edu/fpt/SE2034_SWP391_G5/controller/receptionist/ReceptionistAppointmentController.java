package vn.edu.fpt.SE2034_SWP391_G5.controller.receptionist;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.WalkInBookingRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistWalkInService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ReceptionistAppointmentController {

    private final AppointmentService appointmentService;
    private final ReceptionistService receptionistService;
    private final ReceptionistWalkInService walkInService;
    private final DepartmentService departmentService;
    private final vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceRepository medicalServiceRepository;

    @GetMapping("/receptionist/appointment")
    // Hiển thị toàn bộ danh sách lịch hẹn theo ngày
    public String showAppointmentList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(value = "fromDate", required = false) String fromDateStr,
            @RequestParam(value = "toDate", required = false) String toDateStr,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        LocalDate today = LocalDate.now();
        LocalDate fromDate = null;
        LocalDate toDate = null;

        if (fromDateStr != null && !fromDateStr.trim().isEmpty()) {
            try {
                fromDate = LocalDate.parse(fromDateStr.trim());
            } catch (Exception e) {
                // Fallback to null
            }
        }
        if (toDateStr != null && !toDateStr.trim().isEmpty()) {
            try {
                toDate = LocalDate.parse(toDateStr.trim());
            } catch (Exception e) {
                // Fallback to null
            }
        }

        if (fromDate == null) {
            fromDate = today.minusDays(6);
        }
        if (toDate == null) {
            toDate = today;
        }
        if (page < 0) {
            page = 0;
        }

        int size = 20;

        Page<AppointmentResponse> appointmentPage;

        if (hasSearchOrFilterCondition(search, status)) {
            appointmentPage = appointmentService.searchAppointmentListForReceptionist(search, status, fromDate, toDate, page, size);
        } else {
            appointmentPage = appointmentService.getAppointmentListForReceptionist(fromDate, toDate, page, size);
        }

        List<AppointmentResponse> appointments = appointmentPage.getContent();

        model.addAttribute("appointments", appointments);
        model.addAttribute("appointmentPage", appointmentPage);
        model.addAttribute("appointmentGroups", appointmentService.groupAppointmentsByDate(appointments));
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);

        addReceptionistInfo(model, userDetails);
        addAppointmentStatusCounts(model, fromDate, toDate);

        return "receptionist/appointment/list";
    }

    private boolean hasSearchOrFilterCondition(String search, String status) {
        return (search != null && !search.trim().isEmpty()) || (status != null && !status.trim().isEmpty());
    }

    private void addAppointmentStatusCounts(Model model, LocalDate fromDate, LocalDate toDate) {
        Map<String, Long> statusCounts = appointmentService.getAppointmentStatusCountsInDateRangeForReceptionist(fromDate, toDate);
        model.addAttribute("confirmedCount", statusCounts.get("CONFIRMED"));
        model.addAttribute("waitingCount", statusCounts.get("WAITING"));
        model.addAttribute("examiningCount", statusCounts.get("EXAMINING"));
        model.addAttribute("completedCount", statusCounts.get("COMPLETED"));
        model.addAttribute("cancelledCount", statusCounts.get("CANCELLED"));
        model.addAttribute("noShowCount", statusCounts.get("NO_SHOW"));
    }

    private void addReceptionistInfo(Model model, CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        String fullName = (user.getLastName() + " " + (user.getMiddleName() != null ? user.getMiddleName() + " " : "") + user.getFirstName()).trim().replaceAll("\\s+", " ");
        String avatarText = "";
        if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            avatarText += user.getLastName().substring(0, 1).toUpperCase();
        }
        if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
            avatarText += user.getFirstName().substring(0, 1).toUpperCase();
        }
        model.addAttribute("receptionist", new ReceptionistResponse(user.getId(), fullName, avatarText));
    }

    private void addPageInfo(Model model, String search, String status, LocalDate fromDate, LocalDate toDate) {
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("currentDateTime", getCurrentDateTime());
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm"));
    }

    @GetMapping("/receptionist/appointment/{id}")
    public String showAppointmentDetail(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(id);
        model.addAttribute("appointment", appointment);
        addReceptionistInfo(model, userDetails);
        model.addAttribute("currentDateTime", getCurrentDateTime());
        return "receptionist/appointment/detail";
    }

    @GetMapping("/receptionist/appointment/walk-in")
    public String showWalkInPage(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) LocalDate bookingDate,
            @RequestParam(required = false) Long serviceId,
            @AuthenticationPrincipal CustomUserDetails userDetails, 
            Model model) {
        
        addReceptionistInfo(model, userDetails);
        model.addAttribute("currentDateTime", getCurrentDateTime());
        
        model.addAttribute("phone", phone);
        model.addAttribute("departmentId", departmentId);
        
        if (bookingDate == null) {
            bookingDate = LocalDate.now();
        }
        model.addAttribute("bookingDate", bookingDate);

        if (phone != null && !phone.trim().isEmpty()) {
            Object patientResult = walkInService.searchPatientByPhone(phone);
            if (patientResult instanceof Map) {
                Map<String, Object> result = (Map<String, Object>) patientResult;
                if (result.containsKey("error")) {
                    model.addAttribute("phoneError", result.get("error"));
                } else {
                    model.addAttribute("patientFound", result.get("found"));
                    if ((Boolean) result.get("found")) {
                        model.addAttribute("firstName", result.get("firstName"));
                        model.addAttribute("lastName", result.get("lastName"));
                        model.addAttribute("gender", result.get("gender"));
                    }
                    
                    // Load departments
                    model.addAttribute("departments", departmentService.getAllActiveDepartments());
                    
                    if (departmentId != null) {
                        model.addAttribute("slots", walkInService.getAvailableSlots(departmentId, bookingDate));
                        
                        java.util.List<vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService> services = medicalServiceRepository.findByDepartmentIdAndStatus(departmentId, "ACTIVE");
                        model.addAttribute("services", services);
                        model.addAttribute("serviceId", serviceId);
                    }
                }
            }
        }

        return "receptionist/appointment/walk-in";
    }

    @PostMapping("/receptionist/appointment/walk-in/book")
    public String bookWalkIn(@ModelAttribute WalkInBookingRequest request, RedirectAttributes redirectAttributes) {
        try {
            Long appointmentId = walkInService.createWalkInAppointment(request);
            redirectAttributes.addFlashAttribute("successMessage", "Đã tạo lịch khám trực tiếp và hóa đơn thành công!");
            return "redirect:/receptionist/appointment/" + appointmentId + "/check-in-ticket";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            
            // Redirect back with current values
            redirectAttributes.addAttribute("phone", request.getPhone());
            redirectAttributes.addAttribute("departmentId", request.getDepartmentId());
            redirectAttributes.addAttribute("bookingDate", request.getBookingDate());
            redirectAttributes.addAttribute("serviceId", request.getServiceId());
            
            return "redirect:/receptionist/appointment/walk-in";
        }
    }
}
