package vn.edu.fpt.SE2034_SWP391_G5.controller.receptionist;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.QueueResponse;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReceptionistQueueController {

    private final AppointmentService appointmentService;
    private final ReceptionistService receptionistService;

    @GetMapping("/receptionist/queue")
    public String showQueueBoard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<QueueResponse> queueRooms = appointmentService.getTodayQueueBoard();
        model.addAttribute("queueRooms", queueRooms);
        model.addAttribute("activeMenu", "queue");
        model.addAttribute("receptionist", receptionistService.getReceptionistByUsername(userDetails.getUser().getEmail()));
        return "receptionist/queue/list";
    }
}
