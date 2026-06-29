package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceSummaryResponse {
    private String paymentStatus;
    private Long paymentAmount;
    private BigDecimal totalPaymentAmount;
}
