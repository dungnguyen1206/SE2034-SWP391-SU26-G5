package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AppointmentResponse {

    private Long id;
    private String appointmentCode;
    private LocalTime slotStartTime;
    private LocalTime slotEndTime;
    private String roomNumber;
    private String patientName;
    private String patientPhone;
    private String doctorName;
    private String departmentName;
    private LocalDate bookingDate;
    private String status;

    private Long patientId;
    private Long doctorId;

    private Long serviceId;
    private String serviceName;

    private String shift;

    private String note;
    private LocalDateTime createdAt;
    private boolean hasMedicalRecord;

    public String getSlotText() {
        if (slotStartTime == null || slotEndTime == null) {
            return "";
        }

        return slotStartTime.toString().substring(0, 5)
                + " - "
                + slotEndTime.toString().substring(0, 5);
    }

    public String getRoomText() {
        if (roomNumber == null) {
            return "";
        }

        return roomNumber;
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

    public String getStatusClass() {
        if (status == null) {
            return "status-default";
        }

        return switch (status) {
            case "CONFIRMED" -> "status-confirmed";
            case "WAITING" -> "status-waiting";
            case "EXAMINING" -> "status-examining";
            case "COMPLETED" -> "status-completed";
            case "CANCELLED" -> "status-cancelled";
            case "NO_SHOW" -> "status-no-show";
            default -> "status-default";
        };
    }
}