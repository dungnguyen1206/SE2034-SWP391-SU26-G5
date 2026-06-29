package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceSummaryResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface InvoiceService {
    BigDecimal getTotalAmount(String paymentStatus, int month, int year);

    InvoiceSummaryResponse getInvoiceSummary(String paymentStatus, Integer month, Integer year, LocalDate startDate, LocalDate endDate);

}
