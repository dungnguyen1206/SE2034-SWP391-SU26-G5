package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
@NoArgsConstructor
@Getter
@Setter
public class AppointmentPrintResponse {

    private Long id;
    private String appointmentCode;
    private String patientFullName;
    private String patientPhone;
    private String doctorFullName;
    private String departmentName;
    private String roomNumber;
    private LocalDate bookingDate;
    private LocalTime slotStartTime;
    private LocalTime slotEndTime;
    private LocalDateTime checkInTime;
    private String status;
    private Long queueNumber;

    public AppointmentPrintResponse(
            Long id,
            String appointmentCode,
            String patientName,
            String patientPhone,
            String doctorName,
            String departmentName,
            String roomNumber,
            LocalDate bookingDate,
            LocalTime slotStartTime,
            LocalTime slotEndTime,
            LocalDateTime checkInTime,
            String status
    ) {
        this.id = id;
        this.appointmentCode = appointmentCode;
        this.patientFullName = patientName;
        this.patientPhone = patientPhone;
        this.doctorFullName = doctorName;
        this.departmentName = departmentName;
        this.roomNumber = roomNumber;
        this.bookingDate = bookingDate;
        this.slotStartTime = slotStartTime;
        this.slotEndTime = slotEndTime;
        this.checkInTime = checkInTime;
        this.status = status;
    }

    public String getSlotText() {
        if (slotStartTime == null || slotEndTime == null) {
            return "";
        }
        return slotStartTime.toString().substring(0, 5) + " - " + slotEndTime.toString().substring(0, 5);
    }

    public String getStatusLabel() {
        if (status == null) {
            return "";
        }

        return switch (status) {
            case "CONFIRMED" -> "Đã xác nhận";
            case "WAITING" -> "Chờ khám";
            case "EXAMINING" -> "Đang khám";
            case "COMPLETED" -> "Đã khám xong";
            case "CANCELLED" -> "Đã hủy";
            case "NO_SHOW" -> "Vắng mặt";
            default -> status;
        };
    }
}