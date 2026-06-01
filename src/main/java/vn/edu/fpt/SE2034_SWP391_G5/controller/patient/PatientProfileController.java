package vn.edu.fpt.SE2034_SWP391_G5.controller.patient;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateProfileRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.PatientResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Province;
import vn.edu.fpt.SE2034_SWP391_G5.repository.ProvinceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.PatientService;

import java.util.List;

@Controller
@RequestMapping("/patient/profile")
@RequiredArgsConstructor
public class PatientProfileController {

    private final PatientService patientService;
    private final ProvinceRepository provinceRepository;

    private static final Long DEMO_PATIENT_ID = 14L;

    @GetMapping
    public String viewProfile(Model model) {
        PatientResponse profile = patientService.getProfile(DEMO_PATIENT_ID);
        model.addAttribute("profile", profile);
        return "patient/profile/detail";
    }

    @GetMapping("/edit")
    public String editProfileForm(Model model) {
        PatientResponse profile = patientService.getProfile(DEMO_PATIENT_ID);
        List<Province> provinces = provinceRepository.findAll();

        UpdateProfileRequest form = new UpdateProfileRequest();
        form.setFirstName(profile.getFirstName());
        form.setMiddleName(profile.getMiddleName());
        form.setLastName(profile.getLastName());
        form.setPhone(profile.getPhone());
        form.setGender(profile.getGender());
        form.setDateOfBirth(profile.getDateOfBirth());
        form.setBloodType(profile.getBloodType());
        form.setAddressLine(profile.getAddressLine());

        model.addAttribute("profile", profile);
        model.addAttribute("form", form);
        model.addAttribute("provinces", provinces);
        return "patient/profile/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute("form") UpdateProfileRequest form,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            List<Province> provinces = provinceRepository.findAll();
            PatientResponse profile = patientService.getProfile(DEMO_PATIENT_ID);
            model.addAttribute("profile", profile);
            model.addAttribute("provinces", provinces);
            return "patient/profile/edit";
        }
        try {
            patientService.updateProfile(DEMO_PATIENT_ID, form);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công");
            return "redirect:/patient/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/patient/profile/edit";
        }
    }
}
