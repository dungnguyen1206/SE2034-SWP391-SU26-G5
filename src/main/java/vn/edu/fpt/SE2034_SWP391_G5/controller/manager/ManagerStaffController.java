package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateUserRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorStaffDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistStaffDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.StaffResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.enums.AppointmentStatus;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.RoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetailsService;
import vn.edu.fpt.SE2034_SWP391_G5.service.*;

import java.util.List;

@Controller
@RequestMapping("/manager/staff")
@RequiredArgsConstructor
public class ManagerStaffController {

    private final DoctorService doctorService;
    private final StaffService staffService;
    private final ReceptionistService receptionistService;
    private final DepartmentService departmentService;
    private final ImageUploadService imageUploadService;




    @GetMapping("/list")
    public String staff(@RequestParam(required = false) String role,
                        @RequestParam(required = false) String filterKey,
                        Model model) {
        String selectedRole = ("DOCTOR".equals(role) || "RECEPTIONIST".equals(role)) ? role : null;

        long doctorCount = doctorService.findByRoleNameAndStatus("DOCTOR", "ACTIVE").size();
        long receptionistCount = receptionistService.findByRoleNameAndStatus("RECEPTIONIST", "ACTIVE").size();
        long totalStaff = doctorCount + receptionistCount;

        List<StaffResponse> staffResponses = staffService.findStaff(selectedRole, filterKey);
        int numberOfResult = staffResponses.size();

        model.addAttribute("totalStaff", totalStaff);
        model.addAttribute("doctorCount", doctorCount);
        model.addAttribute("receptionistCount", receptionistCount);
        model.addAttribute("staffResponses", staffResponses);
        model.addAttribute("numberOfResult", numberOfResult);
        model.addAttribute("selectedRole", selectedRole);

        return "manager/staff/list";
    }

    @GetMapping("/{staffId}/edit")
    public String updateStaffForm(@RequestParam String staffRole,
                                  @PathVariable Long staffId,
                                  Model model) {
        UpdateUserRequest updateUserForm;

        if ("DOCTOR".equalsIgnoreCase(staffRole)) {
            updateUserForm = staffService.getDoctorToUpdate(staffId);
        } else {
            updateUserForm = staffService.getReceptionistToUpdate(staffId);
        }

        reloadUpdateFormModel(model, updateUserForm);
        return "manager/staff/form";
    }

    @PostMapping("/{staffId}/edit")
    public String updateStaff(@PathVariable Long staffId,
                              @Valid @ModelAttribute("updateUserForm") UpdateUserRequest updateUserForm,
                              BindingResult bindingResult,
                              Model model, RedirectAttributes redirectAttributes, @RequestParam("avatarFile") MultipartFile avatarFile) {
        updateUserForm.setId(staffId);

        if (bindingResult.hasErrors()) {
            reloadUpdateFormModel(model, updateUserForm);
            return "manager/staff/form";
        }

        try {
            CustomUserDetails customUserDetails =  (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User manager = customUserDetails.getUser();
            updateUserForm.setCreatedBy(manager);
            String avatar = imageUploadService.uploadImage(avatarFile);
            updateUserForm.setAvatar(avatar);
            staffService.updateStaffProfile(staffId, updateUserForm);
        } catch (BadRequestException | ResourceNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            reloadUpdateFormModel(model, updateUserForm);
            return "manager/staff/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ nhân viên thành công");
        return "redirect:/manager/staff/" + staffId + "?staffRole=" + updateUserForm.getStaffRole();
    }

    private void reloadUpdateFormModel(Model model, UpdateUserRequest updateUserForm) {
        boolean isDoctor = "DOCTOR".equalsIgnoreCase(updateUserForm.getProfileType())
                || "DOCTOR".equalsIgnoreCase(updateUserForm.getStaffRole());

        model.addAttribute("departmentList", departmentService.getAllActiveDepartments());
        model.addAttribute("updateUserForm", updateUserForm);
        model.addAttribute("roleName", isDoctor ? "ROLE_DOCTOR" : "ROLE_RECEPTIONIST");
        model.addAttribute("roleLabel", isDoctor ? "Bác sĩ" : "Lễ tân");
    }

    @GetMapping("/{staffId}")
    public String staffDetail(@PathVariable Long staffId,
                              @RequestParam String staffRole,
                              Model model) {

        if (staffRole.equalsIgnoreCase("receptionist")) {
            ReceptionistStaffDetailResponse receptionist = staffService.findReceptionistStaffDetailById(staffId);
            model.addAttribute("staff", receptionist);
            model.addAttribute("receptionist", receptionist);
        } else {
            DoctorStaffDetailResponse doctor = staffService.findDoctorStaffDetailById(staffId);

            Long numberOfUpcomingAppointment = staffService.countDoctorsAppointmentByAppointmentStatus(
                    AppointmentStatus.CONFIRMED.toString(), doctor.getId());
            Long numberOfCompletedAppointment = staffService.countDoctorsAppointmentByAppointmentStatus(
                    AppointmentStatus.COMPLETED.toString(), doctor.getId());
            Long numberOfCancelledAppointment = staffService.countDoctorsAppointmentByAppointmentStatus(
                    AppointmentStatus.CANCELLED.toString(), doctor.getId());
            Long numberOfNoShowAppointment = staffService.countDoctorsAppointmentByAppointmentStatus(
                    AppointmentStatus.NO_SHOW.toString(), doctor.getId());
            Long totalCancelled = numberOfCancelledAppointment + numberOfNoShowAppointment;
            Long totalAppointment = totalCancelled + numberOfCompletedAppointment + numberOfUpcomingAppointment;

            model.addAttribute("totalCancelled", totalCancelled);
            model.addAttribute("totalAppointment", totalAppointment);
            model.addAttribute("totalUpcomingAppointment", numberOfUpcomingAppointment);
            model.addAttribute("totalCompletedAppointment", numberOfCompletedAppointment);
            model.addAttribute("staff", doctor);
            model.addAttribute("doctor", doctor);
        }
        return "manager/staff/detail";
    }
}
