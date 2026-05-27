package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.service.ManagerDashboardService;

@Controller
@RequestMapping("/manager/dashboard")
public class ManagerDashboardController {

    private final ManagerDashboardService managerDashboardService;
    public ManagerDashboardController(ManagerDashboardService managerDashboardService) {
        this.managerDashboardService = managerDashboardService;
    }

    @GetMapping
    public String dashboard(Model model) {
        long totalPatients = managerDashboardService.getTotalPatients();
        model.addAttribute("totalPatients", totalPatients);
        return "manager/dashboard";
    }
}
