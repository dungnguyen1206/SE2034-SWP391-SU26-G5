package vn.edu.fpt.SE2034_SWP391_G5.controller.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalRecord;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalServiceOrder;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalRecordRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceOrderRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorMedicalServiceController {

    private final AppointmentService appointmentService;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final MedicalServiceOrderRepository medicalServiceOrderRepository;

    @PostMapping("/appointments/{id}/services/save")
    @Transactional
    public String saveServices(
            @PathVariable Long id,
            @RequestParam(value = "serviceIds", required = false) List<Long> serviceIds,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Long doctorId = userDetails.getUser().getId();
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(id);

        // Security check
        if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền cập nhật dịch vụ cho lịch hẹn này");
        }

        if ("COMPLETED".equals(appointment.getStatus())) {
            throw new org.springframework.security.access.AccessDeniedException("Cuộc hẹn đã hoàn thành, không thể thêm dịch vụ khám");
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

        if ("COMPLETED".equals(appointment.getStatus())) {
            throw new org.springframework.security.access.AccessDeniedException("Cuộc hẹn đã hoàn thành, không thể cập nhật kết quả dịch vụ");
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

    @PostMapping("/appointments/{id}/services/{orderId}/delete")
    @Transactional
    public String deleteService(
            @PathVariable Long id,
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Long doctorId = userDetails.getUser().getId();
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(id);

        // Security check
        if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền xóa dịch vụ cho lịch hẹn này");
        }

        if ("COMPLETED".equals(appointment.getStatus())) {
            throw new org.springframework.security.access.AccessDeniedException("Cuộc hẹn đã hoàn thành, không thể xóa dịch vụ khám");
        }

        MedicalServiceOrder order = medicalServiceOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chỉ định dịch vụ với ID: " + orderId));

        // Ensure this order belongs to the correct medical record of this appointment
        if (!order.getMedicalRecord().getAppointment().getId().equals(id)) {
            throw new org.springframework.security.access.AccessDeniedException("Chỉ định dịch vụ này không thuộc về lịch hẹn");
        }

        // Only allow deletion if status is PENDING_PAYMENT (unpaid)
        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Dịch vụ đã được thanh toán hoặc đã thực hiện, không thể xóa");
            return "redirect:/doctor/appointments/" + id + "/detail?tab=services";
        }

        medicalServiceOrderRepository.delete(order);

        redirectAttributes.addFlashAttribute("successMessage", "Xóa dịch vụ khám thành công");
        return "redirect:/doctor/appointments/" + id + "/detail?tab=services";
    }
}
