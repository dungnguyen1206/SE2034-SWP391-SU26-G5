package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.domain.Page;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceRowResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceSummaryResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {
    BigDecimal getTotalAmount(String paymentStatus, int month, int year);

    InvoiceSummaryResponse getInvoiceSummary(String paymentStatus, Integer month, Integer year, LocalDate startDate, LocalDate endDate);

    Page<InvoiceRowResponse> invoiceRowResponses (Integer month, Integer year, LocalDate startDate, LocalDate endDate, int page, int size);
}
