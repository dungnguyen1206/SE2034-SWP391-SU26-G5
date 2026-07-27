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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateServiceResultRequest;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalServiceOrderService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorMedicalServiceController {

    private final MedicalServiceOrderService medicalServiceOrderService;

    @PostMapping("/appointments/{id}/services/save")
    public String saveServices(
            @PathVariable Long id,
            @RequestParam(value = "serviceIds", required = false) List<Long> serviceIds,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Long doctorId = userDetails.getUser().getId();

        try {
            medicalServiceOrderService.createServiceOrders(id, doctorId, serviceIds);
            redirectAttributes.addFlashAttribute("successMessage", "Chỉ định dịch vụ khám thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/doctor/appointments/" + id + "/detail?tab=services";
    }

    @PostMapping("/appointments/{id}/services/{orderId}/result")
    public String saveServiceResult(
            @PathVariable Long id,
            @PathVariable Long orderId,
            @Valid @ModelAttribute UpdateServiceResultRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            return "redirect:/doctor/appointments/" + id + "/detail?tab=services";
        }

        Long doctorId = userDetails.getUser().getId();

        try {
            medicalServiceOrderService.updateServiceOrderResult(id, doctorId, orderId, request.getResult(), request.getNote());
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật dịch vụ khám thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/doctor/appointments/" + id + "/detail?tab=services";
    }

    @PostMapping("/appointments/{id}/services/{orderId}/delete")
    public String deleteService(
            @PathVariable Long id,
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Long doctorId = userDetails.getUser().getId();

        try {
            medicalServiceOrderService.deleteServiceOrder(id, doctorId, orderId);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa dịch vụ khám thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/doctor/appointments/" + id + "/detail?tab=services";
    }
}

