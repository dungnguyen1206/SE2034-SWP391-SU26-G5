package vn.edu.fpt.SE2034_SWP391_G5.controller.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorAppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    @GetMapping("/appointment-list")
    public String appointmentList(
            @RequestParam(value = "status", defaultValue = "ALL") String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        Long doctorId = userDetails.getUser().getId();

        // Doctor Info sidebar
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

        // Current date in Vietnamese
        String currentDate = java.time.format.DateTimeFormatter
                .ofPattern("EEEE, 'ngày' dd 'tháng' MM, yyyy", new java.util.Locale("vi", "VN"))
                .format(java.time.LocalDate.now());
        // Capitalize first letter of the day (e.g. "Thứ hai" -> "Thứ Hai")
        if (currentDate.length() > 0) {
            currentDate = Character.toUpperCase(currentDate.charAt(0)) + currentDate.substring(1);
        }
        model.addAttribute("currentDate", currentDate);

        // Pagination setup (5 items per page)
        Pageable pageable = PageRequest.of(page, 5, Sort.by("id").ascending());
        Page<AppointmentResponse> appointmentsPage = appointmentService.getAppointmentsForDoctor(doctorId, status, pageable);

        model.addAttribute("appointments", appointmentsPage);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appointmentsPage.getTotalPages());

        // Range calculation for footer: Hiển thị X - Y trong tổng số Z
        long totalElements = appointmentsPage.getTotalElements();
        long startRow = totalElements == 0 ? 0 : (long) page * 5 + 1;
        long endRow = Math.min((long) (page + 1) * 5, totalElements);
        model.addAttribute("startRow", startRow);
        model.addAttribute("endRow", endRow);
        model.addAttribute("totalElements", totalElements);

        // Tab counts
        model.addAttribute("countAll", appointmentService.countAppointmentsForDoctor(doctorId, "ALL"));
        model.addAttribute("countWaiting", appointmentService.countAppointmentsForDoctor(doctorId, "WAITING"));
        model.addAttribute("countExamining", appointmentService.countAppointmentsForDoctor(doctorId, "IN_PROGRESS"));
        model.addAttribute("countCompleted", appointmentService.countAppointmentsForDoctor(doctorId, "COMPLETED"));

        return "doctor/appointment-list";
    }

    @PostMapping("/appointments/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(value = "currentStatus", defaultValue = "ALL") String currentStatus,
            @RequestParam(value = "page", defaultValue = "0") int page) {
        try {
            appointmentService.updateAppointmentStatus(id, status);
        } catch (Exception e) {
            // Log or handle error if needed
        }
        return "redirect:/doctor/appointment-list?status=" + currentStatus + "&page=" + page;
    }
}

