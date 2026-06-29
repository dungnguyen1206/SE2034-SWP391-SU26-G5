package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRowResponse {
    private Long invoiceId;
    private String invoiceCode;
    private String appointmentCode;
    private String patientName;
    private String doctorName;
    private String departmentName;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime date;

}
