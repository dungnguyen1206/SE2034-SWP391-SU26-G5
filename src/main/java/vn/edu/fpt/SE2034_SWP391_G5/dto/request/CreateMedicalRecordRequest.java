package vn.edu.fpt.SE2034_SWP391_G5.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CreateMedicalRecordRequest {
    private String symptoms;
    private String diagnosis;
    private String conclusion;
    private String prescriptionText;
    private String notes;
    private Integer heartRate;
    private String bloodPressure;
    private BigDecimal bloodGlucose;
    private BigDecimal weight;
}
