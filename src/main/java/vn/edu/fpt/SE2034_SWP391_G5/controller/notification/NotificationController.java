package vn.edu.fpt.SE2034_SWP391_G5.controller.notification;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.NotificationService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistService;
import vn.edu.fpt.SE2034_SWP391_G5.service.PatientService;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.PatientResponse;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final DoctorService doctorService;
    private final ReceptionistService receptionistService;
    private final PatientService patientService;

    public NotificationController(NotificationService notificationService,
                                  DoctorService doctorService,
                                  ReceptionistService receptionistService,
                                  PatientService patientService) {
        this.notificationService = notificationService;
        this.doctorService = doctorService;
        this.receptionistService = receptionistService;
        this.patientService = patientService;
    }

    // 1. Mở trang Danh sách thông báo
    @GetMapping("/list")
    public String listNotifications(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            // Lấy danh sách thông báo nhét vào Model
            model.addAttribute("notifications", notificationService.getAllForUser(userDetails.getUser()));

            // Đọc Role của User
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            // Điều hướng về đúng trang HTML của từng Actor
            // Role names không có prefix ROLE_ (đã đổi: MANAGER, DOCTOR, PATIENT, RECEPTIONIST)
            if (role.equals("MANAGER") || role.equals("ROLE_MANAGER")) return "manager/notifications";
            if (role.equals("DOCTOR") || role.equals("ROLE_DOCTOR")) {
                try {
                    DoctorResponse doctor = doctorService.getDoctorById(userDetails.getUser().getId());
                    model.addAttribute("doctorName", doctor.getFullName() != null ? doctor.getFullName().trim() : "Bác sĩ");
                    model.addAttribute("doctorDept", doctor.getDepartmentName() != null ? doctor.getDepartmentName() : "");
                } catch (Exception e) {
                    model.addAttribute("doctorName", "Bác sĩ");
                    model.addAttribute("doctorDept", "");
                }
                model.addAttribute("doctorAvatar", userDetails.getUser().getAvatar());
                return "doctor/notifications";
            }
            if (role.equals("PATIENT") || role.equals("ROLE_PATIENT")) {
                model.addAttribute("profile", patientService.getProfile(userDetails.getUser().getId()));
                return "patient/notifications";
            }
            if (role.equals("RECEPTIONIST") || role.equals("ROLE_RECEPTIONIST")) {
                model.addAttribute("receptionist", receptionistService.getReceptionistById(userDetails.getUser().getId()));
                return "receptionist/notifications";
            }
            if (role.equals("ADMIN") || role.equals("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            }
        }
        return "redirect:/login"; // Nếu lỗi thì đá về đăng nhập
    }

    // 2. Bấm vào 1 thông báo để đánh dấu đã đọc (và chuyển hướng tạm)
    @PostMapping("/{id}/read")
    public String markAsRead(@PathVariable Long id, HttpServletRequest request) {
        notificationService.markAsRead(id);

        // Lấy link trang web hiện tại (referer) để đọc xong thì ở lại trang đó, hoặc về /notifications/list
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/notifications/list");
    }

    // 3. Đánh dấu tất cả là đã đọc
    @PostMapping("/read-all")
    public String markAllAsRead(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletRequest request) {
        if (userDetails != null) {
            notificationService.markAllAsRead(userDetails.getUser());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/notifications/list");
    }
}