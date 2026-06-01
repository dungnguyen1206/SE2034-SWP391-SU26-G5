package vn.edu.fpt.SE2034_SWP391_G5.controller.patient;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateAppointmentRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleSlotResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;

import java.util.List;

@Controller
@RequestMapping("/patient/appointments")
@RequiredArgsConstructor
public class PatientAppointmentController {

    private final AppointmentService appointmentService;
    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    private final MedicalServiceRepository medicalServiceRepository;

    // TODO: thay bằng @AuthenticationPrincipal khi auth sẵn sàng
    private static final Long DEMO_PATIENT_ID = 14L;

    @GetMapping
    public String listAppointments(Model model) {
        List<AppointmentResponse> appointments =
                appointmentService.getAppointmentsByPatient(DEMO_PATIENT_ID);
        model.addAttribute("appointments", appointments);
        return "patient/appointments/list";
    }

    @GetMapping("/{id}")
    public String appointmentDetail(@PathVariable Long id, Model model) {
        AppointmentResponse appointment =
                appointmentService.getAppointmentDetail(id, DEMO_PATIENT_ID);
        model.addAttribute("appointment", appointment);
        return "patient/appointments/detail";
    }

    @GetMapping("/book")
    public String selectDepartment(Model model) {
        List<Department> departments = departmentService.getAllActiveDepartments();
        model.addAttribute("departments", departments);
        return "patient/appointments/book-step1";
    }

    @GetMapping("/book/step2")
    public String bookStep2(@RequestParam Integer departmentId,
                            @RequestParam(required = false) Long doctorId,
                            Model model) {
        Department department = departmentService.getDepartmentById(departmentId);
        List<DoctorResponse> doctors = doctorService.getDoctorsByDepartment(departmentId);
        List<MedicalService> services = medicalServiceRepository
                .findByDepartmentIdAndStatus(departmentId, "ACTIVE");

        model.addAttribute("department", department);
        model.addAttribute("doctors", doctors);
        model.addAttribute("services", services);
        model.addAttribute("selectedDoctorId", doctorId);
        model.addAttribute("bookRequest", new CreateAppointmentRequest());

        if (doctorId != null) {
            List<ScheduleSlotResponse> schedules = appointmentService.getAvailableSchedules(doctorId);
            DoctorResponse selectedDoctor = doctorService.getDoctorById(doctorId);
            model.addAttribute("schedules", schedules);
            model.addAttribute("selectedDoctor", selectedDoctor);
        }

        return "patient/appointments/book-step2";
    }

    @PostMapping("/book")
    public String confirmBooking(@Valid @ModelAttribute("bookRequest") CreateAppointmentRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin");
            return "redirect:/patient/appointments/book/step2?doctorId=" + request.getDoctorId();
        }
        try {
            AppointmentResponse result = appointmentService.bookAppointment(DEMO_PATIENT_ID, request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đặt lịch thành công! Mã lịch hẹn: " + result.getAppointmentCode());
            return "redirect:/patient/appointments/" + result.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/patient/appointments/book/step2?doctorId=" + request.getDoctorId();
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointment(id, DEMO_PATIENT_ID);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy lịch hẹn thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/patient/appointments";
    }
}

