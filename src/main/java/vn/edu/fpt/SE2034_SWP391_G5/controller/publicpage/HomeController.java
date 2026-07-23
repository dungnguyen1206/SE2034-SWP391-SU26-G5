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
    public String getHomePage(Model model) {
        model.addAttribute("departments", departmentService.getAllActiveDepartments());
        return "public/home";
    }

    @GetMapping("/departments")
    public String listDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllActiveDepartments());
        return "public/departments/list";
    }

    @GetMapping("/departments/{id}")
    public String detailDepartment(@org.springframework.web.bind.annotation.PathVariable Integer id, Model model) {
        vn.edu.fpt.SE2034_SWP391_G5.entity.Department department = departmentService.getDepartmentById(id);
        model.addAttribute("department", department);
        return "public/departments/detail";
    }
}
