package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.StaffResponse;
import vn.edu.fpt.SE2034_SWP391_G5.service.impl.DoctorServiceImpl;
import vn.edu.fpt.SE2034_SWP391_G5.service.impl.ReceptionistServiceImpl;
import vn.edu.fpt.SE2034_SWP391_G5.service.impl.StaffServiceImpl;

import java.util.List;

@Controller
@RequestMapping("/manager/staff")
public class ManagerStaffController {
    private final DoctorServiceImpl doctorServiceImpl;
    private final StaffServiceImpl staffServiceImpl;
    private final ReceptionistServiceImpl receptionistServiceImpl;
    public ManagerStaffController(DoctorServiceImpl doctorServiceImpl, ReceptionistServiceImpl receptionistServiceImpl,
                                  StaffServiceImpl staffServiceImpl) {
        this.doctorServiceImpl = doctorServiceImpl;
        this.receptionistServiceImpl = receptionistServiceImpl;
        this.staffServiceImpl = staffServiceImpl;
    }

    @GetMapping
    public String staff(@RequestParam(required = false) String role, Model  model) {
         String selectedRole = ("DOCTOR".equals(role) || "RECEPTIONIST".equals(role)) ? role : null;
         
         long doctorCount = doctorServiceImpl.findByRoleNameAndStatus("DOCTOR", "ACTIVE").size();
         long receptionistCount = receptionistServiceImpl.findByRoleNameAndStatus("RECEPTIONIST", "ACTIVE").size();
         long totalStaff  = doctorCount + receptionistCount;
         List<StaffResponse> staffResponses = staffServiceImpl.getAllActiveStaff(selectedRole);
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
