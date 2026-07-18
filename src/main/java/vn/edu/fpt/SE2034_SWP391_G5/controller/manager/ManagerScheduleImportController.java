package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleImportResult;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.exception.Schedule.ScheduleConflictException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.Schedule.ScheduleImportException;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.ScheduleImportService;

@Controller
@RequestMapping("/manager/schedules")
@RequiredArgsConstructor
public class ManagerScheduleImportController {

    private final ScheduleImportService scheduleImportService;

    @PostMapping("/import")
    public String importSchedules(@RequestParam("weekScheduleId") Long weekScheduleId, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("weekScheduleId", weekScheduleId);

        try {
            User manager = getAuthenticatedManager();

            ScheduleImportResult result = scheduleImportService.importSchedules(file, weekScheduleId, manager.getId());

            if (!result.isSuccessful()) {
                redirectAttributes.addFlashAttribute("scheduleImportResult", result);

                redirectAttributes.addFlashAttribute("importErrorMessage", "File Excel có dữ liệu không hợp lệ. " + "Không có lịch nào được import.");

                return "redirect:/manager/schedules/list";
            }

            redirectAttributes.addFlashAttribute("importSuccessMessage", "Đã import thành công " + result.getImportedRows() + " lịch làm việc.");

        } catch (ScheduleImportException | ScheduleConflictException exception) {
            /*
             * Nếu ScheduleService phát sinh conflict trong lúc lưu,
             * transaction trong ImportService đã rollback trước khi
             * exception đi tới controller.
             */
            redirectAttributes.addFlashAttribute("importErrorMessage", exception.getMessage());
        }

        return "redirect:/manager/schedules/list";
    }

    private User getAuthenticatedManager() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new ScheduleImportException("Không xác định được Manager đang đăng nhập.");
        }

        return userDetails.getUser();
    }
}