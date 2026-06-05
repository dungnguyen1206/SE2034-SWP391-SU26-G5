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
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalServiceService;

import java.util.List;

@Controller
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientDoctorController {

    private final DoctorService doctorService;
    private final DepartmentService departmentService;
    private final MedicalServiceService medicalServiceService;

    // ---- Danh sách bác sĩ ----
    @GetMapping("/doctors")
    public String listDoctors(@RequestParam(required = false) Integer departmentId, Model model) {
        List<Department> departments = departmentService.getAllActiveDepartments();
        List<DoctorResponse> doctors;

        if (departmentId != null) {
            doctors = doctorService.getDoctorsByDepartment(departmentId);
            model.addAttribute("selectedDepartmentId", departmentId);
        } else {
            // Lấy tất cả bác sĩ active từ tất cả khoa
            doctors = departments.stream()
                    .flatMap(d -> doctorService.getDoctorsByDepartment(d.getId()).stream())
                    .toList();
            model.addAttribute("selectedDepartmentId", null);
        }

        model.addAttribute("doctors", doctors);
        model.addAttribute("departments", departments);
        return "patient/doctors/list";
    }

    // ---- Chi tiết bác sĩ ----
    @GetMapping("/doctors/{id}")
    public String doctorDetail(@PathVariable Long id, Model model) {
        DoctorResponse doctor = doctorService.getDoctorById(id);
        model.addAttribute("doctor", doctor);
        return "patient/doctors/detail";
    }

    // ---- Danh sách khoa ----
    @GetMapping("/departments")
    public String listDepartments(Model model) {
        List<Department> departments = departmentService.getAllActiveDepartments();
        model.addAttribute("departments", departments);
        return "patient/departments/list";
    }

    // ---- Chi tiết khoa ----
    @GetMapping("/departments/{id}")
    public String departmentDetail(@PathVariable Integer id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        List<DoctorResponse> doctors = doctorService.getDoctorsByDepartment(id);
        model.addAttribute("department", department);
        model.addAttribute("doctors", doctors);
        return "patient/departments/detail";
    }
    // ---- dịch vụ theo khoa-----
    @GetMapping("/services")
    public String listServices(@RequestParam(required = false) Integer departmentId, Model model) {
        List<Department> departments = departmentService.getAllActiveDepartments();
        List<MedicalService> services = medicalServiceService.getMedicalServicelistByDepartment(departmentId);
        model.addAttribute("services", services);
        model.addAttribute("departments", departments);
        model.addAttribute("selectedDepartmentId", departmentId);
        return "patient/services/list";
    }

}
