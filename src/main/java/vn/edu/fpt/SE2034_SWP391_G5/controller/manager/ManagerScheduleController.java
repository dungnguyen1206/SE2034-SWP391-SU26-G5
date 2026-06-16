package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateDoctorScheduleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorScheduleResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.RoomResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Room;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.WeekSchedule;
import vn.edu.fpt.SE2034_SWP391_G5.repository.DepartmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.WeekScheduleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/manager/schedules")
@RequiredArgsConstructor
public class ManagerScheduleController {

    private final DepartmentService departmentService;
    private final WeekScheduleService weekScheduleService;
    private final RoomService roomService;
    private final DoctorService doctorService;
    private final ScheduleService scheduleService;

    @GetMapping("/list")
    public String scheduleList(@RequestParam(required = false) Long weekScheduleId,@RequestParam(required = false)Integer departmentId, Model model) {
        List<Department> departments = departmentService.getAllActiveDepartments();
        WeekSchedule presentWeek = null;
        if (weekScheduleId == null) {
            presentWeek = weekScheduleService.findPresentWeekSchedule();

        }
        else {
            presentWeek = weekScheduleService.findWeekScheduleById(weekScheduleId);
        }

        List<RoomResponse> rooms = roomService.getAllRooms();
        List<DoctorResponse> doctors = doctorService.getAllDoctors();
        List<LocalDate> workdates = weekScheduleService.workdates(presentWeek.getId());
        WeekSchedule nextWeek = weekScheduleService.findNextWeekSchedule(presentWeek);
        WeekSchedule prevWeek = weekScheduleService.findPreviousWeekSchedule(presentWeek);

        model.addAttribute("prevWeekId", prevWeek != null ? prevWeek.getId() : null);
        model.addAttribute("nextWeekId", nextWeek != null ? nextWeek.getId() : null);
        model.addAttribute("workdates", workdates);
        model.addAttribute("presentWeek", presentWeek);
        model.addAttribute("departments", departments);
        model.addAttribute("rooms", rooms);
        model.addAttribute("doctors", doctors);
        model.addAttribute("scheduleRequest", new CreateDoctorScheduleRequest());
        return "manager/schedules/list";
    }

    @PostMapping("/create")
    public String createSchedule(@ModelAttribute CreateDoctorScheduleRequest createDoctorScheduleRequest,
                                 @RequestParam("weekScheduleId") Long weekScheduleId ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        DoctorScheduleResponse doctorScheduleResponse = scheduleService.createDoctorSchedule(createDoctorScheduleRequest,user.getId(), weekScheduleId);

        return "redirect:/manager/schedules/list";


    }

}
