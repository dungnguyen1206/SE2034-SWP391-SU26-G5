package vn.edu.fpt.SE2034_SWP391_G5.controller.manager;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateDoctorScheduleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.DoctorScheduleUpdateRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorScheduleResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorScheduleRowResponse;
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
    public String scheduleList(@RequestParam(required = false) Long weekScheduleId,
                               @RequestParam(required = false)Integer departmentId,
                               @RequestParam(required = false) String doctorName,
                               @RequestParam(required = false) String shift,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               Model model) {
        WeekSchedule presentWeek = null;
        if (weekScheduleId == null) {
            presentWeek = weekScheduleService.findPresentWeekSchedule();

        }
        else {
            presentWeek = weekScheduleService.findWeekScheduleById(weekScheduleId);
        }
        Page<DoctorScheduleRowResponse> doctorScheduleRowResponsePage =scheduleService.doctorScheduleRowResponse(presentWeek.getId(),departmentId,doctorName,shift,page,size);
        List<Department> departments = departmentService.getAllActiveDepartments();
        List<RoomResponse> rooms = roomService.getAllRooms();
        List<DoctorResponse> doctors = doctorService.getAllDoctors();
        List<LocalDate> workdates = weekScheduleService.workdates(presentWeek.getId());
        WeekSchedule nextWeek = weekScheduleService.findNextWeekSchedule(presentWeek);
        WeekSchedule prevWeek = weekScheduleService.findPreviousWeekSchedule(presentWeek);
        model.addAttribute("presentWeek", presentWeek);
        model.addAttribute("prevWeekId", prevWeek != null ? prevWeek.getId() : null);
        model.addAttribute("nextWeekId", nextWeek != null ? nextWeek.getId() : null);
        model.addAttribute("doctorScheduleRowResponsePage",doctorScheduleRowResponsePage);
        model.addAttribute("workdates", workdates);
        model.addAttribute("departments", departments);
        model.addAttribute("rooms", rooms);
        model.addAttribute("doctors", doctors);
        model.addAttribute("scheduleRequest", new CreateDoctorScheduleRequest());
        return "manager/schedules/list";
    }

    @PostMapping("/{weekScheduleId}/list")
    public String updateSchedule(@PathVariable Long weekScheduleId,
                                 @RequestParam("action") String action,
                                 @RequestParam(required = false)Integer departmentId,
                                 @RequestParam(required = false) String doctorName,
                                 @RequestParam(required = false) String shift,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 RedirectAttributes redirectAttributes) {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        WeekSchedule presentWeek =scheduleService.updateWeekSchedule(weekScheduleId,action,user.getId());
        redirectAttributes.addAttribute("weekScheduleId", weekScheduleId);
        if (departmentId != null) redirectAttributes.addAttribute("departmentId", departmentId);
        if (doctorName != null) redirectAttributes.addAttribute("doctorName", doctorName);
        if (shift != null) redirectAttributes.addAttribute("shift", shift);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);

        return "redirect:/manager/schedules/list";
    }


    @PostMapping("/create")
    public String createSchedule(@ModelAttribute CreateDoctorScheduleRequest createDoctorScheduleRequest,
                                 @RequestParam("weekScheduleId") Long weekScheduleId, RedirectAttributes redirectAttributes ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        DoctorScheduleResponse doctorScheduleResponse = scheduleService.createDoctorSchedule(createDoctorScheduleRequest,user.getId(), weekScheduleId);
        redirectAttributes.addAttribute("weekScheduleId", weekScheduleId);
        redirectAttributes.addFlashAttribute("doctorScheduleResponse", doctorScheduleResponse);
        redirectAttributes.addFlashAttribute("successMessage","Tạo ca làm việc thành công");
        return "redirect:/manager/schedules/list";


    }


    //Update  doctor schedule

    @PostMapping("/update")
    public String updateDoctorSchedule(@RequestParam Long weekScheduleId,
                                       @RequestParam(required = false) Integer departmentId,
                                       @RequestParam(required = false) String doctorNameFilter,
                                       @RequestParam(required = false) String doctorName,
                                       @RequestParam(required = false) String departmentName,
                                       @RequestParam(required = false) String shift,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size,
                                       @Valid @ModelAttribute DoctorScheduleUpdateRequest doctorScheduleUpdateRequest,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes, Model model) {

        if (bindingResult.hasErrors()) {
            // Có lỗi xảy ra -> Đẩy ngược lại toàn bộ dữ liệu hidden vào model để giữ trạng thái cho giao diện
            model.addAttribute("weekScheduleId", weekScheduleId);
            model.addAttribute("departmentId", departmentId);
            model.addAttribute("doctorNameFilter", doctorNameFilter);
            model.addAttribute("doctorName", doctorName);
            model.addAttribute("departmentName", departmentName);
            model.addAttribute("shift", shift);
            model.addAttribute("page", page);
            model.addAttribute("size", size);

            // Nạp lại các danh sách xổ xuống (Dropdown)
            WeekSchedule presentWeek = weekScheduleService.findWeekScheduleById(weekScheduleId);
            model.addAttribute("presentWeek", presentWeek);

            List<LocalDate> workDates = weekScheduleService.workdates(weekScheduleId);
            model.addAttribute("workDates", workDates);

            //departmentID ở đây phải theo department name
            if (departmentName != null) {
                List<RoomResponse> rooms = roomService.getRoomsByDepartmentId(Long.valueOf(departmentService.getDepartmentByName(departmentName).getId()));
                model.addAttribute("rooms", rooms);
            }

            return "manager/schedules/update";
        }

        // Nếu thành công -> Redirect về trang list kèm các filter cũ để giữ bộ lọc cho người dùng
        scheduleService.updateDoctorSchedule(doctorScheduleUpdateRequest, weekScheduleId);

        redirectAttributes.addAttribute("weekScheduleId", weekScheduleId);
        if (departmentId != null) redirectAttributes.addAttribute("departmentId", departmentId);
        if (doctorNameFilter != null) redirectAttributes.addAttribute("doctorName", doctorNameFilter);
        if (shift != null) redirectAttributes.addAttribute("shift", shift);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);

        return "redirect:/manager/schedules/list";
    }

    @GetMapping("/update")
    public String updateDoctorSchedule(@RequestParam Long weekScheduleId,
                                       @RequestParam(required = false) Long  doctorScheduleId,
                                       @RequestParam(required = false)Integer departmentId,
                                       @RequestParam(required = false) String doctorName,
                                       @RequestParam(required = false) String doctorNameFilter,
                                       @RequestParam(required = false) String departmentName,
                                       @RequestParam(required = false) String shift,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size,Model model){
        WeekSchedule presentWeek = weekScheduleService.findWeekScheduleById(weekScheduleId);
        Department department = departmentService.getDepartmentByName(departmentName);
        List<RoomResponse> rooms = roomService.getRoomsByDepartmentId(Long.valueOf(department.getId()));
        DoctorScheduleUpdateRequest doctorScheduleUpdateRequest = scheduleService.getDoctorScheduleUpdateRequest(doctorScheduleId);
        List<LocalDate> workDates = weekScheduleService.workdates(weekScheduleId);
        model.addAttribute("doctorScheduleUpdateRequest", doctorScheduleUpdateRequest);
        model.addAttribute("rooms", rooms);
        model.addAttribute("doctorNameFilter", doctorNameFilter);
        model.addAttribute("doctorName", doctorName);
        model.addAttribute("departmentName",departmentName);
        model.addAttribute("workDates", workDates);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("presentWeek", presentWeek);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "manager/schedules/update";
    }

}
