package vn.edu.fpt.SE2034_SWP391_G5.controller.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorScheduleReportResponse;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.ScheduleService;

import java.time.LocalDate;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/schedule")
    public String schedule(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        Long doctorId = userDetails.getUser().getId();

        DoctorScheduleReportResponse report = scheduleService.getWeeklyScheduleReport(doctorId, targetDate);

        model.addAttribute("weekSchedule", report.getWeekSchedule());
        model.addAttribute("totalHoursStr", report.getTotalHoursStr());
        model.addAttribute("shiftCountStr", report.getShiftCountStr());
        model.addAttribute("performance", report.getPerformance());
        model.addAttribute("prevWeekDate", report.getPrevWeekDate());
        model.addAttribute("nextWeekDate", report.getNextWeekDate());

        return "doctor/schedule";
    }
}
