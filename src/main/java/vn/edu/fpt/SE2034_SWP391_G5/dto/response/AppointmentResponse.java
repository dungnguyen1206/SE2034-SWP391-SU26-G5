package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponse {
    private String appointmentCode;
    private String patientName;
    private String doctorName;
    private String serviceName;
    private LocalTime startTime;
    private LocalTime endTime;
    private String roomNumber;
    private String status;

}
