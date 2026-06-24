package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueResponse {
    private String roomNumber;
    private String departmentName;
    private String doctorFullName;
    private int totalWaiting;
    private PatientInfo examiningPatient;
    private List<PatientInfo> waitingPatients;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientInfo {
        private Integer stt;
        private String appointmentCode;
        private String patientName;
        private LocalTime checkInTime;
        private String status;
    }
}