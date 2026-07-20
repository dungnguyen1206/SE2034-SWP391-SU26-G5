package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MedicalRecordResponse {

    private Long id;
    private Long appointmentId;
    private String appointmentCode;

    private String doctorFullName;
    private String doctorDegree;
    private String departmentName;
    private String serviceName;

    private LocalDateTime examinationDate;
    private String symptoms;
    private String diagnosis;
    private String conclusion;
    private String prescriptionText;
    private String notes;

    private Integer heartRate;
    private String bloodPressure;
    private BigDecimal bloodGlucose;
    private BigDecimal weight;

    private String status;

    private java.util.List<ServiceOrderInfo> serviceOrders;

    @Getter
    @Setter
    @Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ServiceOrderInfo {
        private Long id;
        private String serviceName;
        private String result;
        private String status;
        private String note;
        private BigDecimal price;
    }
}
