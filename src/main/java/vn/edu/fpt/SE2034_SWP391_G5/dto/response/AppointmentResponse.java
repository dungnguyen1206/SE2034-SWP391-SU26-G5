package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class AppointmentResponse {

    private Long id;
    private String appointmentCode;
    private String status;

    // Patient info
    private Long patientId;
    private String patientFullName;

    //LinhNH 01/06/2026
    private Integer patientAge;
    private String patientGender;
    private String patientInitials;

    // Doctor info
    private Long doctorId;
    private String doctorFullName;
    private String doctorDegree;
    private String departmentName;

    // Service info
    private Long serviceId;
    private String serviceName;

    // Schedule info
    private LocalDate bookingDate;
    private String shift;
    private LocalTime slotStartTime;
    private LocalTime slotEndTime;
    private String roomNumber;

    // Other
    private String note;
    private LocalDateTime createdAt;
    private boolean hasMedicalRecord;
}
