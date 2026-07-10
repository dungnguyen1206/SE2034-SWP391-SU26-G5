package vn.edu.fpt.SE2034_SWP391_G5.controller.receptionist;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.WalkInBookingRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ReceptionistWalkInService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/receptionist/api/walk-in")
@RequiredArgsConstructor
public class ReceptionistWalkInRestController {

    private final ReceptionistWalkInService walkInService;
    private final DepartmentService departmentService;

    @GetMapping("/departments")
    public ResponseEntity<?> getDepartments() {
        List<Map<String, Object>> departments = departmentService.getAllActiveDepartments().stream()
                .map(d -> Map.<String, Object>of(
                        "id", d.getId(),
                        "name", d.getName()
                ))
                .toList();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/patient")
    public ResponseEntity<?> searchPatient(@RequestParam String phone) {
        Object response = walkInService.searchPatientByPhone(phone);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available-slots")
    public ResponseEntity<?> getAvailableSlots(
            @RequestParam Integer departmentId,
            @RequestParam java.time.LocalDate date) {
        return ResponseEntity.ok(walkInService.getAvailableSlots(departmentId, date));
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookWalkIn(@Valid @RequestBody WalkInBookingRequest request) {
        try {
            walkInService.createWalkInAppointment(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã tạo lịch khám trực tiếp và hóa đơn thành công!"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
