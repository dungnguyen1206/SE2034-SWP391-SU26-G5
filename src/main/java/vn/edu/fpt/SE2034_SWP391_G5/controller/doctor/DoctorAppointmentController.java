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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalRecord;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalRecordRepository;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorAppointmentController {

    private final AppointmentService appointmentService;
    private final MedicalRecordRepository medicalRecordRepository;

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
            @RequestParam(value = "date", required = false) String dateStr,
            RedirectAttributes redirectAttributes) {

        String redirectUrl = "redirect:/doctor/appointment-list?status=" + currentStatus + "&page=" + page;
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            redirectUrl += "&date=" + dateStr;
        }

        // Prevent modification if already COMPLETED
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(id);
        if (appointment != null && "COMPLETED".equals(appointment.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cuộc hẹn đã hoàn thành không thể thay đổi trạng thái.");
            return redirectUrl;
        }

        if ("COMPLETED".equals(status)) {
            // Check if medical record is fully completed
            Optional<MedicalRecord> medicalRecordOpt = medicalRecordRepository.findByAppointmentId(id);
            if (medicalRecordOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể chuyển trạng thái sang Hoàn thành vì hồ sơ bệnh án chưa được tạo.");
                return redirectUrl;
            }

            MedicalRecord record = medicalRecordOpt.get();
            if (record.getSymptoms() == null || record.getSymptoms().trim().isEmpty() ||
                record.getDiagnosis() == null || record.getDiagnosis().trim().isEmpty() ||
                record.getBloodPressure() == null || record.getBloodPressure().trim().isEmpty() ||
                record.getWeight() == null ||
                record.getConclusion() == null || record.getConclusion().trim().isEmpty() ||
                record.getPrescriptionText() == null || record.getPrescriptionText().trim().isEmpty() ||
                record.getNotes() == null || record.getNotes().trim().isEmpty() ||
                record.getBloodGlucose() == null ||
                record.getHeartRate() == null) {

                redirectAttributes.addFlashAttribute("errorMessage", "Không thể chuyển trạng thái sang Hoàn thành vì hồ sơ bệnh án chưa đầy đủ thông tin.");
                return redirectUrl;
            }
        }

        try {
            appointmentService.updateAppointmentStatus(id, status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }

        return redirectUrl;
    }
}
