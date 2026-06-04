package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateStaffRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorOnDutyResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorStaffDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ReceptionistStaffDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.StaffResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.enums.AppointmentStatus;
import vn.edu.fpt.SE2034_SWP391_G5.repository.DepartmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.RoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistService;
import vn.edu.fpt.SE2034_SWP391_G5.service.StaffService;
import vn.edu.fpt.SE2034_SWP391_G5.service.impl.DoctorServiceImpl;
import vn.edu.fpt.SE2034_SWP391_G5.service.impl.ReceptionistServiceImpl;
import vn.edu.fpt.SE2034_SWP391_G5.service.impl.StaffServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/manager/staff")
public class ManagerStaffController {
    private final DoctorService doctorService;
    private final StaffService staffService;
    private final ReceptionistService receptionistService;
    private final DepartmentService departmentService;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    ;

    public ManagerStaffController(DoctorService doctorService
            , StaffService staffService, ReceptionistService receptionistService
            , DepartmentService departmentService,  RoleRepository roleRepository,  UserRoleRepository userRoleRepository) {
        this.doctorService = doctorService;
        this.staffService = staffService;
        this.receptionistService = receptionistService;
        this.departmentService = departmentService;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @GetMapping("/list")
    public String staff(@RequestParam(required = false) String role,
                        @RequestParam(required = false) String filterKey, Model model) {
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

    @GetMapping("/create")
    public String createStaffForm(@RequestParam(defaultValue = "doctor") String type, Model model) {
        String staffType = "receptionist".equalsIgnoreCase(type) ? "receptionist" : "doctor";

        CreateStaffRequest createStaffForm = new CreateStaffRequest();
        createStaffForm.setStaffType(staffType);
        List<Department> departmentList = departmentService.getAllActiveDepartments();
        model.addAttribute("departmentList", departmentList);
        model.addAttribute("createStaffForm", createStaffForm);
        model.addAttribute("staffType", staffType);
        model.addAttribute("roleName", "doctor".equals(staffType) ? "ROLE_DOCTOR" : "ROLE_RECEPTIONIST");
        model.addAttribute("roleLabel", "doctor".equals(staffType) ? "Bác sĩ" : "Lễ tân");

        return "manager/staff/form";
    }


    @PostMapping("/create")
    public String createStaff(@Valid @ModelAttribute("createStaffForm") CreateStaffRequest createStaffForm,
                              BindingResult bindingResult,
                              Model model) {

        if (bindingResult.hasErrors()) {
            List<Department> departmentList = departmentService.getAllActiveDepartments();
            model.addAttribute("departmentList", departmentList);
            model.addAttribute("createStaffForm", createStaffForm);
            model.addAttribute("staffType", createStaffForm.getStaffType());
            model.addAttribute("roleName", "doctor".equals(createStaffForm.getStaffType()) ? "ROLE_DOCTOR" : "ROLE_RECEPTIONIST");
            model.addAttribute("roleLabel", "doctor".equals(createStaffForm.getStaffType()) ? "Bác sĩ" : "Lễ tân");
            return "manager/staff/form";
        }

        staffService.createStaff(createStaffForm, bindingResult);
        if (bindingResult.hasErrors()) {
            List<Department> departmentList = departmentService.getAllActiveDepartments();
            model.addAttribute("departmentList", departmentList);
            model.addAttribute("createStaffForm", createStaffForm);
            model.addAttribute("staffType", createStaffForm.getStaffType());
            model.addAttribute("roleName", "doctor".equals(createStaffForm.getStaffType()) ? "ROLE_DOCTOR" : "ROLE_RECEPTIONIST");
            model.addAttribute("roleLabel", "doctor".equals(createStaffForm.getStaffType()) ? "Bác sĩ" : "Lễ tân");
            return "manager/staff/form";
        }

        return "redirect:/manager/staff/list";
    }


    @GetMapping("/{staffId}")
    public String staffDetail(@PathVariable Long staffId, @RequestParam String staffRole, Model model) {

        if (staffRole.equalsIgnoreCase("receptionist")) {
            ReceptionistStaffDetailResponse receptionist = staffService.findReceptionistStaffDetailById(staffId);
            model.addAttribute("staff", receptionist);
            model.addAttribute("receptionist", receptionist);
        } else {
            DoctorStaffDetailResponse doctor = staffService.findDoctorStaffDetailById(staffId);

            Long numberOfUpcomingAppointment = staffService.countDoctorsAppointmentByAppointmentStatus(AppointmentStatus.CONFIRMED.toString(), doctor.getId());
            Long numberOfCompletedAppointment = staffService.countDoctorsAppointmentByAppointmentStatus(AppointmentStatus.COMPLETED.toString(), doctor.getId());
            Long numberOfCancelledAppointment = staffService.countDoctorsAppointmentByAppointmentStatus(AppointmentStatus.CANCELLED.toString(), doctor.getId());
            Long numberOfNoShowAppointment = staffService.countDoctorsAppointmentByAppointmentStatus(AppointmentStatus.NO_SHOW.toString(), doctor.getId());
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
