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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import jakarta.validation.Valid;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import java.time.LocalDate;
import java.util.Locale;
import java.util.stream.Collectors;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalRecord;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalServiceOrder;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalRecordRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceOrderRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalRecordService;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateMedicalRecordRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateMedicalRecordRequest;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorAppointmentController {

    private final AppointmentService appointmentService;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordService medicalRecordService;
    private final MedicalServiceRepository medicalServiceRepository;
    private final MedicalServiceOrderRepository medicalServiceOrderRepository;

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
            @RequestParam(value = "tab", required = false, defaultValue = "info") String tab,
            @RequestParam(value = "action", required = false) String action,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        Long doctorId = userDetails.getUser().getId();
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(id);
        
        // Security check: ensure this appointment belongs to the logged-in doctor
        if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền xem thông tin lịch hẹn này");
        }
        
        Optional<MedicalRecord> medicalRecordOpt = medicalRecordRepository.findByAppointmentId(id);
        model.addAttribute("medicalRecord", medicalRecordOpt.orElse(null));
        
        List<MedicalService> activeServices = medicalServiceRepository.findByStatus("ACTIVE");
        model.addAttribute("activeServices", activeServices);
        
        model.addAttribute("appointment", appointment);
        model.addAttribute("tab", tab);
        model.addAttribute("action", action);
        return "doctor/patient-detail";
    }

    @PostMapping("/appointments/{id}/services/save")
    @Transactional
    public String saveServices(
            @PathVariable Long id,
            @RequestParam(value = "serviceIds", required = false) List<Long> serviceIds,
            jakarta.servlet.http.HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        Long doctorId = userDetails.getUser().getId();
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(id);
        
        // Security check
        if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền cập nhật dịch vụ cho lịch hẹn này");
        }
        
        MedicalRecord medicalRecord = medicalRecordRepository.findByAppointmentId(id)
                .orElse(null);
                
        if (medicalRecord == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể thêm dịch vụ. Vui lòng tạo hồ sơ bệnh án trước.");
            return "redirect:/doctor/appointments/" + id + "/detail?tab=services";
        }
        
        if (serviceIds != null && !serviceIds.isEmpty()) {
            for (Long sId : serviceIds) {
                // Check if this service is already ordered
                boolean alreadyExists = medicalRecord.getMedicalServiceOrders() != null &&
                        medicalRecord.getMedicalServiceOrders().stream()
                                .anyMatch(o -> o.getMedicalService().getId().equals(sId));
                                
                if (alreadyExists) {
                    continue;
                }
                
                MedicalService service = medicalServiceRepository.findById(sId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ với ID: " + sId));
                
                MedicalServiceOrder order = MedicalServiceOrder.builder()
                        .medicalRecord(medicalRecord)
                        .medicalService(service)
                        .priceReference(service.getReferencePrice())
                        .status("PENDING_PAYMENT")
                        .note(null)
                        .createAt(LocalDateTime.now())
                        .updateAt(LocalDateTime.now())
                        .build();
                        
                medicalServiceOrderRepository.save(order);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Chỉ định dịch vụ khám thành công");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn ít nhất một dịch vụ");
        }
        
        return "redirect:/doctor/appointments/" + id + "/detail?tab=services";
    }

    @PostMapping("/appointments/{id}/records/create")
    public String createMedicalRecord(
            @PathVariable Long id,
            @Valid @ModelAttribute CreateMedicalRecordRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            redirectAttributes.addFlashAttribute("tempRecord", request);
            return "redirect:/doctor/appointments/" + id + "/detail?tab=info&action=create";
        }

        Long doctorId = userDetails.getUser().getId();
        try {
            medicalRecordService.createMedicalRecord(id, request, doctorId);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo hồ sơ bệnh án thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("tempRecord", request);
            return "redirect:/doctor/appointments/" + id + "/detail?tab=info&action=create";
        }
        return "redirect:/doctor/appointments/" + id + "/detail?tab=info";
    }

    @PostMapping("/appointments/{id}/records/update")
    public String updateMedicalRecord(
            @PathVariable Long id,
            @Valid @ModelAttribute UpdateMedicalRecordRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            redirectAttributes.addFlashAttribute("tempRecord", request);
            return "redirect:/doctor/appointments/" + id + "/detail?tab=info&action=edit";
        }

        Long doctorId = userDetails.getUser().getId();
        try {
            medicalRecordService.updateMedicalRecord(id, request, doctorId);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ bệnh án thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("tempRecord", request);
            return "redirect:/doctor/appointments/" + id + "/detail?tab=info&action=edit";
        }
        return "redirect:/doctor/appointments/" + id + "/detail?tab=info";
    }

    @PostMapping("/appointments/{id}/services/{orderId}/result")
    @Transactional
    public String saveServiceResult(
            @PathVariable Long id,
            @PathVariable Long orderId,
            @RequestParam(value = "result", required = false) String result,
            @RequestParam(value = "note", required = false) String note,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        Long doctorId = userDetails.getUser().getId();
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(id);
        
        // Security check
        if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền cập nhật kết quả cho lịch hẹn này");
        }
        
        MedicalServiceOrder order = medicalServiceOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chỉ định dịch vụ với ID: " + orderId));
                
        // Ensure this order belongs to the correct medical record of this appointment
        if (!order.getMedicalRecord().getAppointment().getId().equals(id)) {
            throw new org.springframework.security.access.AccessDeniedException("Chỉ định dịch vụ này không thuộc về lịch hẹn");
        }
        
        // Ensure the service order is paid (status is not PENDING_PAYMENT and not CANCELLED)
        if ("PENDING_PAYMENT".equals(order.getStatus())) {
            throw new org.springframework.security.access.AccessDeniedException("Dịch vụ chưa được thanh toán, không thể cập nhật kết quả");
        }
        if ("CANCELLED".equals(order.getStatus())) {
            throw new org.springframework.security.access.AccessDeniedException("Dịch vụ đã bị hủy, không thể cập nhật kết quả");
        }

        // Update result, note and status
        order.setNote(note);
        order.setResult(result);
        if (result != null && !result.trim().isEmpty()) {
            if (!"PENDING_PAYMENT".equals(order.getStatus()) && !"CANCELLED".equals(order.getStatus())) {
                order.setStatus("COMPLETED");
            }
        }
        order.setUpdateAt(LocalDateTime.now());
        
        medicalServiceOrderRepository.save(order);
        
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật dịch vụ khám thành công");
        return "redirect:/doctor/appointments/" + id + "/detail?tab=services";
    }
}


