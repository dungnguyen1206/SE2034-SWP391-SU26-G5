package vn.edu.fpt.SE2034_SWP391_G5.controller.patient;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateProfileRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateUserRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.PatientResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Province;
import vn.edu.fpt.SE2034_SWP391_G5.repository.ProvinceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.PatientService;
import vn.edu.fpt.SE2034_SWP391_G5.service.StaffService;

import java.util.List;

@Controller
@RequestMapping("/patient/profile")
@RequiredArgsConstructor
public class PatientProfileController {

    private final PatientService patientService;
    private final StaffService staffService;
    private final ProvinceRepository provinceRepository;

    @GetMapping
    public String viewProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        PatientResponse profile = patientService.getProfile(userDetails.getUser().getId());
        model.addAttribute("profile", profile);
        return "patient/profile/detail";
    }

    @GetMapping("/edit")
    public String editProfileForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long patientId = userDetails.getUser().getId();
        UpdateUserRequest updateUserForm = staffService.getPatientToUpdate(patientId);
        model.addAttribute("updateUserForm", updateUserForm);
        model.addAttribute("provinces", provinceRepository.findAll());
        return "patient/profile/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute("updateUserForm") UpdateUserRequest updateUserForm,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        Long patientId = userDetails.getUser().getId();
        updateUserForm.setProfileType("PATIENT");
        updateUserForm.setStaffRole("PATIENT");

        if (bindingResult.hasErrors()) {
            reloadEditModel(model, patientId, updateUserForm);
            return "patient/profile/edit";
        }

        try {
            patientService.updateProfile(patientId,updateUserForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công");
            return "redirect:/patient/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/patient/profile/edit";
        }
    }

    private void reloadEditModel(Model model, Long patientId, UpdateUserRequest updateUserForm) {
        PatientResponse profile = patientService.getProfile(patientId);
        List<Province> provinces = provinceRepository.findAll();

        model.addAttribute("profile", profile);
        model.addAttribute("updateUserForm", updateUserForm);
        model.addAttribute("provinces", provinces);
    }

}
