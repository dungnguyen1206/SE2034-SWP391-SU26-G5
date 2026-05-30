package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager/staff")
public class ManagerStaffController {

    @GetMapping
    public String staff() {
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
