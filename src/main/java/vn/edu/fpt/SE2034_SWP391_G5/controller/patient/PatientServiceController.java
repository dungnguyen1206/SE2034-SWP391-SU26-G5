package vn.edu.fpt.SE2034_SWP391_G5.controller.patient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalServiceService;

import java.util.List;

@Controller
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientServiceController {

    private final MedicalServiceService medicalServiceService;
    private final DepartmentService departmentService;
    private final MedicalServiceRepository medicalServiceRepository;

    @GetMapping("/services")
    public String listServices(
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        List<Department> departments = departmentService.getAllActiveDepartments();
        List<MedicalService> allServices = medicalServiceService.getMedicalServicelistByDepartment(departmentId);

        int pageSize = 12;
        int totalItems = allServices.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        List<MedicalService> services = allServices.subList(start, end);

        model.addAttribute("departments", departments);
        model.addAttribute("services", services);
        model.addAttribute("selectedDepartmentId", departmentId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "patient/services/list";
    }

    @GetMapping("/services/{id}")
    public String serviceDetail(@PathVariable Long id, Model model) {
        MedicalService service = medicalServiceRepository.findMedicalServiceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ y tế với ID: " + id));

        model.addAttribute("service", service);
        return "patient/services/detail";
    }
}
