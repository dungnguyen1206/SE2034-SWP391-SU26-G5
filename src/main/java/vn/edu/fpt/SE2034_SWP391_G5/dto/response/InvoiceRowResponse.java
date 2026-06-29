package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRowResponse {
    private Long invoiceId;
    private Long appointmentId;
    private String patientName;
    private String departmentName;
    private BigDecimal totalAmount;
    private String status;
    private LocalDate date;

}
