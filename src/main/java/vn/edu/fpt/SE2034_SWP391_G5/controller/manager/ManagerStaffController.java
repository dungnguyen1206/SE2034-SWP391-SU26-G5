package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.StaffResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
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

    public ManagerStaffController(DoctorService doctorService, StaffService staffService, ReceptionistService receptionistService) {
        this.doctorService = doctorService;
        this.staffService = staffService;
        this.receptionistService = receptionistService;
    }

    @GetMapping("/list")
    public String staff(@RequestParam(required = false) String role,
                        @RequestParam(required = false) String filterKey,Model  model) {
         String selectedRole = ("DOCTOR".equals(role) || "RECEPTIONIST".equals(role)) ? role : null;

         
         long doctorCount = doctorService.findByRoleNameAndStatus("DOCTOR", "ACTIVE").size();
         long receptionistCount = receptionistService.findByRoleNameAndStatus("RECEPTIONIST", "ACTIVE").size();
         long totalStaff  = doctorCount + receptionistCount;

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

        model.addAttribute("staffType", staffType);
        model.addAttribute("roleName", "doctor".equals(staffType) ? "ROLE_DOCTOR" : "ROLE_RECEPTIONIST");
        model.addAttribute("roleLabel", "doctor".equals(staffType) ? "Bác sĩ" : "Lễ tân");

        return "manager/staff/form";
    }
}
