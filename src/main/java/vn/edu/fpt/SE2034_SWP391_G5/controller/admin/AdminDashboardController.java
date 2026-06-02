package vn.edu.fpt.SE2034_SWP391_G5.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @GetMapping({"", "/", "/dashboard"})
    public String adminDashboard() {
        return "redirect:/admin/account-list";
    }
}
