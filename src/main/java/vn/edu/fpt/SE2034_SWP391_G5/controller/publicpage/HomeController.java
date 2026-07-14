package vn.edu.fpt.SE2034_SWP391_G5.controller.publicpage;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DepartmentService departmentService;

    @GetMapping({"/", "/home"})
    public String getHomePage(Model model, Authentication authentication) {
        // Nếu user đã login, kiểm tra role
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String role = authority.getAuthority();

                // Staff roles nên redirect về dashboard của họ
                switch (role) {
                    case "ROLE_ADMIN":
                        return "redirect:/admin/account-list";
                    case "ROLE_MANAGER":
                        return "redirect:/manager/dashboard";
                    case "ROLE_DOCTOR":
                        return "redirect:/doctor/dashboard";
                    case "ROLE_RECEPTIONIST":
                        return "redirect:/receptionist/dashboard";
                    case "ROLE_PATIENT":
                        // Patient được phép vào home
                        model.addAttribute("isPatient", true);
                        break;
                }
            }
        }

        model.addAttribute("departments", departmentService.getAllActiveDepartments());
        return "public/home";
    }
}
