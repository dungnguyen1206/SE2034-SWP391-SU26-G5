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

import org.springframework.data.domain.Page;
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
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        List<Department> departments = departmentService.getAllActiveDepartments();
        
        Page<DoctorResponse> doctorPage = 
                doctorService.getActiveDoctorsPaginated(departmentId, search, page, size);

        model.addAttribute("doctors", doctorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", doctorPage.getTotalPages());
        model.addAttribute("totalItems", doctorPage.getTotalElements());
        model.addAttribute("selectedDepartmentId", departmentId);
        model.addAttribute("searchQuery", search != null ? search : "");
        model.addAttribute("departments", departments);
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
