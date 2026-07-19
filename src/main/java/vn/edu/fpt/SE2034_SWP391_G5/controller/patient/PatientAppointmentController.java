package vn.edu.fpt.SE2034_SWP391_G5.controller.patient;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateAppointmentRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleSlotJsonResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleSlotResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;
import vn.edu.fpt.SE2034_SWP391_G5.service.PatientService;

import java.util.List;

@Controller
@RequestMapping("/patient/appointments")
@RequiredArgsConstructor
public class PatientAppointmentController {

    private final AppointmentService appointmentService;
    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    private final MedicalServiceRepository medicalServiceRepository;
    private final PatientService patientService;

    // TODO: thay bằng @AuthenticationPrincipal khi auth sẵn sàng
    // private static final Long DEMO_PATIENT_ID = 14L;

    @GetMapping
    // public String listAppointments(Model model) {
    //     List<AppointmentResponse> appointments = appointmentService.getAppointmentsByPatient(DEMO_PATIENT_ID);
    public String listAppointments(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<AppointmentResponse> appointments =
                appointmentService.getAppointmentsByPatient(userDetails.getUser().getId());
        model.addAttribute("appointments", appointments);
        return "patient/appointments/list";
    }

    @GetMapping("/{id}")
    // public String appointmentDetail(@PathVariable Long id, Model model) {
    //     AppointmentResponse appointment = appointmentService.getAppointmentDetail(id, DEMO_PATIENT_ID);
    public String appointmentDetail(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    Model model) {
        AppointmentResponse appointment =
                appointmentService.getAppointmentDetail(id, userDetails.getUser().getId());
        model.addAttribute("appointment", appointment);
        return "patient/appointments/detail";
    }

    @GetMapping("/book")
    public String selectDepartment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (!patientService.isProfileComplete(userDetails.getUser().getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng cập nhật đầy đủ thông tin cá nhân (Họ, tên, giới tính, ngày sinh, số điện thoại, tỉnh/thành phố, địa chỉ) trước khi đặt lịch.");
            return "redirect:/patient/profile/edit";
        }
        List<Department> departments = departmentService.getAllActiveDepartments();
        model.addAttribute("departments", departments);
        return "patient/appointments/book-step1";
    }

    @GetMapping("/book/step2")
    public String bookStep2(@RequestParam Integer departmentId,
                            @RequestParam(required = false) Long doctorId,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (!patientService.isProfileComplete(userDetails.getUser().getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng cập nhật đầy đủ thông tin cá nhân (Họ, tên, giới tính, ngày sinh, số điện thoại, tỉnh/thành phố, địa chỉ) trước khi đặt lịch.");
            return "redirect:/patient/profile/edit";
        }
        Department department = departmentService.getDepartmentById(departmentId);
        List<DoctorResponse> doctors = doctorService.getDoctorsByDepartment(departmentId);
        
        // Không cần lấy danh sách services nữa vì sẽ dùng service mặc định
        // List<MedicalService> services = medicalServiceRepository
        //         .findByDepartmentIdAndStatus(departmentId, "ACTIVE");

        model.addAttribute("department", department);
        model.addAttribute("doctors", doctors);
        // model.addAttribute("services", services); // Không cần nữa
        model.addAttribute("selectedDoctorId", doctorId);
        // Trước: model.addAttribute("bookRequest", new CreateAppointmentRequest());
        //        → doctorId và departmentId null → hidden fields render value="" → POST gửi null
        CreateAppointmentRequest bookRequest = new CreateAppointmentRequest();
        bookRequest.setDoctorId(doctorId);
        bookRequest.setDepartmentId(departmentId);
        model.addAttribute("bookRequest", bookRequest);

        // Lấy dịch vụ lâm sàng mặc định để hiển thị giá
        MedicalService clinicalService = medicalServiceRepository
                .findFirstByDepartmentIdAndNameContainingIgnoreCaseAndStatus(departmentId, "tổng quát", "ACTIVE")
                .or(() -> medicalServiceRepository.findFirstByDepartmentIdAndNameContainingIgnoreCaseAndStatus(departmentId, "khám", "ACTIVE"))
                .or(() -> {
                    List<MedicalService> list = medicalServiceRepository.findByDepartmentIdAndStatus(departmentId, "ACTIVE");
                    return list.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(list.get(0));
                })
                .orElse(null);
        model.addAttribute("clinicalService", clinicalService);

        if (doctorId != null) {
            List<ScheduleSlotResponse> schedules = appointmentService.getAvailableSchedules(doctorId);
            DoctorResponse selectedDoctor = doctorService.getDoctorById(doctorId);

            List<ScheduleSlotJsonResponse> schedulesJson = schedules.stream()
                    .map(s -> ScheduleSlotJsonResponse.builder()
                            .scheduleId(s.getScheduleId())
                            .workDate(s.getWorkDate() != null ? s.getWorkDate().toString() : "")
                            .shift(s.getShift())
                            .shiftLabel(s.getShiftLabel())
                            .roomNumber(s.getRoomNumber())
                            .slots(s.getSlots() == null ? List.of() : s.getSlots().stream()
                                    .map(sl -> ScheduleSlotJsonResponse.SlotInfo.builder()
                                            .slotId(sl.getSlotId())
                                            .startTime(sl.getStartTime() != null
                                                    ? sl.getStartTime().toString().substring(0, 5) : "")
                                            .endTime(sl.getEndTime() != null
                                                    ? sl.getEndTime().toString().substring(0, 5) : "")
                                            .bookedCapacity(sl.getBookedCapacity())
                                            .maxCapacity(sl.getMaxCapacity())
                                            .status(sl.getStatus())
                                            .available(sl.isAvailable())
                                            .build())
                                    .toList())
                            .build())
                    .toList();

            model.addAttribute("schedules", schedulesJson);
            model.addAttribute("selectedDoctor", selectedDoctor);
        } else {
            model.addAttribute("schedules", List.of());
        }

        return "patient/appointments/book-step2";
    }

    @PostMapping("/book")
    // public String confirmBooking(...) { ... appointmentService.bookAppointment(DEMO_PATIENT_ID, request); }
    public String confirmBooking(@Valid @ModelAttribute("bookRequest") CreateAppointmentRequest request,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        if (!patientService.isProfileComplete(userDetails.getUser().getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng cập nhật đầy đủ thông tin cá nhân (Họ, tên, giới tính, ngày sinh, số điện thoại, tỉnh/thành phố, địa chỉ) trước khi đặt lịch.");
            return "redirect:/patient/profile/edit";
        }
        // Trước đây redirect về step2?doctorId=... thiếu departmentId → MissingServletRequestParameterException
        String step2Url = "/patient/appointments/book/step2?departmentId=" + request.getDepartmentId()
                + "&doctorId=" + request.getDoctorId();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin");
            return "redirect:" + step2Url;
        }
        
        // Tự động gán dịch vụ "Khám lâm sàng" cho department này
        if (request.getServiceId() == null) {
            try {
                MedicalService defaultService = medicalServiceRepository
                        .findFirstByDepartmentIdAndNameContainingIgnoreCaseAndStatus(request.getDepartmentId(), "tổng quát", "ACTIVE")
                        .or(() -> medicalServiceRepository.findFirstByDepartmentIdAndNameContainingIgnoreCaseAndStatus(request.getDepartmentId(), "khám", "ACTIVE"))
                        .or(() -> {
                            List<MedicalService> list = medicalServiceRepository.findByDepartmentIdAndStatus(request.getDepartmentId(), "ACTIVE");
                            return list.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(list.get(0));
                        })
                        .orElseThrow(() -> new RuntimeException(
                                "Không tìm thấy dịch vụ khám lâm sàng cho khoa này"));
                request.setServiceId(defaultService.getId());
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Lỗi hệ thống: " + e.getMessage());
                return "redirect:" + step2Url;
            }
        }
        
        try {
            AppointmentResponse result = appointmentService.bookAppointment(
                    userDetails.getUser().getId(), request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đặt lịch thành công! Mã lịch hẹn: " + result.getAppointmentCode());
            return "redirect:/patient/appointments/" + result.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:" + step2Url;
        }
    }

    @PostMapping("/{id}/cancel")
    // public String cancelAppointment(@PathVariable Long id, ...) { appointmentService.cancelAppointment(id, DEMO_PATIENT_ID); }
    public String cancelAppointment(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointment(id, userDetails.getUser().getId());
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy lịch hẹn thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/patient/appointments";
    }
}
