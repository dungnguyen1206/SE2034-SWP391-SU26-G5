package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager/dashboard")
public class ManagerDashboardController {

    @GetMapping
    public String dashboard() {
        return "manager/dashboard";
    }
}
