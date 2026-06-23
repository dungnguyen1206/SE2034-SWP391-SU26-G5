package vn.edu.fpt.SE2034_SWP391_G5.controller.publicpage;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;

@Controller
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final DepartmentService departmentService;

    @GetMapping
    public String getHomePage(Model model) {
        model.addAttribute("departments", departmentService.getAllActiveDepartments());
        return "public/home";
    }
}
