package vn.edu.fpt.SE2034_SWP391_G5.controller.publicpage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;

import java.util.List;

/**
 * Controller cho trang công khai - Departments
 * Bất kỳ ai cũng có thể xem (không cần đăng nhập)
 */
@Controller
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicDepartmentController {

    private final DepartmentService departmentService;
    private final DoctorService doctorService;

    /**
     * Danh sách chuyên khoa (PUBLIC - không cần đăng nhập)
     */
    @GetMapping("/departments")
    public String listDepartments(Model model) {
        List<Department> departments = departmentService.getAllActiveDepartments();
        model.addAttribute("departments", departments);
        return "public/departments/list";
    }

    /**
     * Chi tiết chuyên khoa (PUBLIC - không cần đăng nhập)
     */
    @GetMapping("/departments/{id}")
    public String departmentDetail(@PathVariable Integer id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        if (department == null) {
            throw new IllegalArgumentException("Không tìm thấy khoa");
        }
        
        var doctors = doctorService.getDoctorsByDepartment(id);
        
        model.addAttribute("department", department);
        model.addAttribute("doctors", doctors);
        return "public/departments/detail";
    }
}
