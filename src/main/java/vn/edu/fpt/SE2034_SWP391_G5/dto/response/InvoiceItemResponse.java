package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class InvoiceItemResponse {
    private Long id;
    private String itemName;
    private BigDecimal priceApplied;
    private Integer quantity;
    private BigDecimal lineTotal;
}
