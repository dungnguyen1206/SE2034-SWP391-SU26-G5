package vn.edu.fpt.SE2034_SWP391_G5.controller.doctor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateMedicalRecordRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateMedicalRecordRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateUserRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalRecordService;
import vn.edu.fpt.SE2034_SWP391_G5.service.PatientService;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorMedicalRecordController {

    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;
    private final PatientService patientService;

    @PostMapping("/appointments/{id}/records/create")
    public String createMedicalRecord(
            @PathVariable Long id,
            @Valid @ModelAttribute CreateMedicalRecordRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        Long doctorId = userDetails.getUser().getId();
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(id);
        if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền tạo hồ sơ bệnh án cho lịch hẹn này");
        }
        if (!"EXAMINING".equals(appointment.getStatus()) && !"IN_PROGRESS".equals(appointment.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ cho phép tạo hồ sơ bệnh án khi trạng thái cuộc hẹn đang ở 'Đang khám'");
            return "redirect:/doctor/appointments/" + id + "/detail?tab=info";
        }

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            redirectAttributes.addFlashAttribute("tempRecord", request);
            return "redirect:/doctor/appointments/" + id + "/detail?tab=info&action=create";
        }

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
        Long doctorId = userDetails.getUser().getId();
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(id);
        if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền chỉnh sửa hồ sơ bệnh án cho lịch hẹn này");
        }
        if ("COMPLETED".equals(appointment.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cuộc hẹn đã hoàn thành, không thể chỉnh sửa hồ sơ bệnh án");
            return "redirect:/doctor/appointments/" + id + "/detail?tab=info";
        }

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            redirectAttributes.addFlashAttribute("tempRecord", request);
            return "redirect:/doctor/appointments/" + id + "/detail?tab=info&action=edit";
        }

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

    @PostMapping("/appointments/{id}/patient/update")
    public String updatePatientInfo(
            @PathVariable Long id,
            @ModelAttribute UpdateUserRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Long doctorId = userDetails.getUser().getId();
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(id);

        if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền cập nhật thông tin lịch hẹn này");
        }

        if ("COMPLETED".equals(appointment.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cuộc hẹn đã hoàn thành, không thể cập nhật thông tin bệnh nhân");
            return "redirect:/doctor/appointments/" + id + "/detail?tab=info";
        }

        try {
            patientService.updateNullProfileFields(appointment.getPatientId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin bệnh nhân thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/doctor/appointments/" + id + "/detail?tab=info";
    }
}
