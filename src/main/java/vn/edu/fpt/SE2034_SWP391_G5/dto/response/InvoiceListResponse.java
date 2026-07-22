package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class InvoiceListResponse {
    private Long appointmentId;
    private Integer stt;
    private String appointmentCode;
    private String patientFullName;
    private String phone;
    private BigDecimal displayAmount;
    private String paymentStatus;
    private LocalDateTime createdAt;
}
