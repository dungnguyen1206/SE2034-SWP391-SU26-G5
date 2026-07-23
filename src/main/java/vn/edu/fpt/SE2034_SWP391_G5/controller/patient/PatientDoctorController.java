package vn.edu.fpt.SE2034_SWP391_G5.controller.patient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    // ---- Danh sách bác sĩ (yêu cầu login - ROLE_PATIENT) ----
    @GetMapping("/doctors")
    public String listDoctors(
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        List<Department> departments = departmentService.getAllActiveDepartments();
        List<DoctorResponse> allDoctors;

        if (departmentId != null) {
            allDoctors = doctorService.getDoctorsByDepartment(departmentId);
            model.addAttribute("selectedDepartmentId", departmentId);
        } else {
            allDoctors = departments.stream()
                    .flatMap(d -> doctorService.getDoctorsByDepartment(d.getId()).stream())
                    .toList();
            model.addAttribute("selectedDepartmentId", null);
        }

        int pageSize = 12;
        int totalItems = allDoctors.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        List<DoctorResponse> doctors = allDoctors.subList(start, end);

        model.addAttribute("doctors", doctors);
        model.addAttribute("departments", departments);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "patient/doctors/list";
    }

    // ---- Chi tiết bác sĩ (yêu cầu login - ROLE_PATIENT) ----
    @GetMapping("/doctors/{id}")
    public String doctorDetail(@PathVariable Long id, Model model) {
        DoctorResponse doctor = doctorService.getDoctorById(id);
        model.addAttribute("doctor", doctor);
        return "patient/doctors/detail";
    }

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
