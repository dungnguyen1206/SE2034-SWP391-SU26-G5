package vn.edu.fpt.SE2034_SWP391_G5.controller.patient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;

import java.util.List;

@Controller
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientDoctorController {

    private final DoctorService doctorService;
    private final DepartmentService departmentService;

    // ---- Danh sách khoa (public) ----
    @GetMapping("/departments")
    public String listDepartments(Model model) {
        List<Department> departments = departmentService.getAllActiveDepartments();
        model.addAttribute("departments", departments);
        return "patient/departments/list";
    }

    // ---- Chi tiết khoa (public) ----
    @GetMapping("/departments/{id}")
    public String departmentDetail(@PathVariable Integer id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        List<DoctorResponse> doctors = doctorService.getDoctorsByDepartment(id);
        model.addAttribute("department", department);
        model.addAttribute("doctors", doctors);
        return "patient/departments/detail";
    }
}
