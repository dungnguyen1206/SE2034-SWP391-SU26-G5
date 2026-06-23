package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueResponse {
    private String roomNumber;
    private String appointmentCode;
    private String patientName;
    private String status;
    private LocalTime startTime;
    private LocalDateTime checkInTime;
    private String doctorName;
}