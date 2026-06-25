package vn.edu.fpt.SE2034_SWP391_G5.controller.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import java.time.LocalDate;
import java.util.Locale;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorAppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/appointment-list")
    public String appointmentList(
            @RequestParam(value = "status", defaultValue = "ALL") String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "date", required = false) String dateStr,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        Long doctorId = userDetails.getUser().getId();
        
        LocalDate bookingDate;
        if (dateStr == null || dateStr.trim().isEmpty()) {
            bookingDate = LocalDate.now();
        } else {
            try {
                bookingDate = LocalDate.parse(dateStr);
            } catch (Exception e) {
                bookingDate = LocalDate.now();
            }
        }
        model.addAttribute("selectedDate", bookingDate.toString());

        // Current date in Vietnamese
        String currentDate = java.time.format.DateTimeFormatter
                .ofPattern("EEEE, 'ngày' dd 'tháng' MM, yyyy", Locale.of("vi", "VN"))
                .format(bookingDate);
        // Capitalize first letter of the day (e.g. "Thứ hai" -> "Thứ Hai")
        if (currentDate.length() > 0) {
            currentDate = Character.toUpperCase(currentDate.charAt(0)) + currentDate.substring(1);
        }
        model.addAttribute("currentDate", currentDate);

        // Pagination setup (5 items per page)
        Pageable pageable = PageRequest.of(page, 5, Sort.by("id").ascending());
        Page<AppointmentResponse> appointmentsPage = appointmentService.getAppointmentsForDoctor(doctorId, bookingDate, status, pageable);

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
        model.addAttribute("countAll", appointmentService.countAppointmentsForDoctor(doctorId, bookingDate, "ALL"));
        model.addAttribute("countWaiting", appointmentService.countAppointmentsForDoctor(doctorId, bookingDate, "WAITING"));
        model.addAttribute("countExamining", appointmentService.countAppointmentsForDoctor(doctorId, bookingDate, "EXAMINING"));
        model.addAttribute("countCompleted", appointmentService.countAppointmentsForDoctor(doctorId, bookingDate, "COMPLETED"));

        return "doctor/appointment-list";
    }

    @PostMapping("/appointments/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(value = "currentStatus", defaultValue = "ALL") String currentStatus,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "date", required = false) String dateStr) {
        try {
            appointmentService.updateAppointmentStatus(id, status);
        } catch (Exception e) {
            // Log or handle error if needed
        }
        
        String redirectUrl = "redirect:/doctor/appointment-list?status=" + currentStatus + "&page=" + page;
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            redirectUrl += "&date=" + dateStr;
        }
        return redirectUrl;
    }

    @GetMapping("/appointments/{id}/detail")
    public String appointmentDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        Long doctorId = userDetails.getUser().getId();
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(id);
        
        // Security check: ensure this appointment belongs to the logged-in doctor
        if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền xem thông tin lịch hẹn này");
        }
        
        model.addAttribute("appointment", appointment);
        return "doctor/patient-detail";
    }
}


