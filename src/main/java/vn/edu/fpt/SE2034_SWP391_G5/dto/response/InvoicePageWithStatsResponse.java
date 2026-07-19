package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoicePageWithStatsResponse {
    private Page<InvoiceListResponse> page;
    private long totalPaidCount;
    private long totalUnpaidCount;
}
