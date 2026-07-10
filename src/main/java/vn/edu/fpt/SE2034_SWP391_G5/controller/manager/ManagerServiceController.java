package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import com.cloudinary.Cloudinary;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateMedicalServiceRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.MedicalServiceResponseForManager;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;

import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ImageUploadService;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalServiceService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager/services")
public class ManagerServiceController {

    private final DepartmentService departmentService;
    private final MedicalServiceService medicalService;
    private final ImageUploadService imageUploadService;

    @GetMapping("/list")
    public ModelAndView getService(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "8") int size,
                                   @RequestParam(required = false) Integer departmentId,
                                   @RequestParam(required = false) String filterKey) {

        ModelAndView mv = new ModelAndView("manager/services/list");
        List<Department> departmentList = departmentService.getAllActiveDepartments();
        Page<MedicalServiceResponseForManager> medicalServiceResponseForManagers = medicalService.getMedicalServiceResponsesByFilter(filterKey, departmentId, page, size);

//            paging
        mv.addObject("medicalServiceResponseForManagers", medicalServiceResponseForManagers.getContent());
        mv.addObject("totalPages", medicalServiceResponseForManagers.getTotalPages());
        mv.addObject("currentPage", page);
        mv.addObject("totalService", medicalServiceResponseForManagers.getTotalElements());
        mv.addObject("departmentId", departmentId);
        mv.addObject("filterKey", filterKey);

        mv.addObject("departmentList", departmentList);
        return mv;
    }


//    update service

    @GetMapping("/update")
    public String updateService(Model model, @RequestParam("serviceId") Long medicalServiceId) {
        UpdateMedicalServiceRequest updateMedicalServiceRequest = medicalService.getMedicalServiceById(medicalServiceId);
        model.addAttribute("updateMedicalServiceRequest", updateMedicalServiceRequest);
        return "manager/services/form";
    }

    @PostMapping("/update")
    public String updateMedicalService(@Valid @ModelAttribute UpdateMedicalServiceRequest updateMedicalServiceRequest,
                                       BindingResult bindingResult, Model model,
                                       RedirectAttributes redirectAttributes, @RequestParam(value = "itemImage", required = false) MultipartFile file) {
        if (bindingResult.hasErrors()) {
            UpdateMedicalServiceRequest freshFromDb = medicalService
                    .getMedicalServiceById(updateMedicalServiceRequest.getServiceId());
            updateMedicalServiceRequest.setDepartment(freshFromDb.getDepartment());
            updateMedicalServiceRequest.setMedicalServiceName(freshFromDb.getMedicalServiceName());
            model.addAttribute("updateMedicalServiceRequest", updateMedicalServiceRequest);
            return "manager/services/form";
        }
        if (file != null) {
            String imageURL = imageUploadService.uploadImage(file);
            updateMedicalServiceRequest.setImageUrl(imageURL);
        }
        medicalService.saveMedicalServiceRequest(updateMedicalServiceRequest);
        redirectAttributes.addAttribute("serviceId", updateMedicalServiceRequest.getServiceId());
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công dịch vụ " + updateMedicalServiceRequest.getMedicalServiceName());
        return "redirect:/manager/services/update";
    }
}
