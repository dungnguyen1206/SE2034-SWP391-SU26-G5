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
    private Long id;
    private String invoiceCode;
    private String patientFullName;
    private String appointmentCode;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
