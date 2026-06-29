package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.domain.Page;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceDetailResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceListResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceSummaryResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface InvoiceService {
    BigDecimal getTotalAmount(String paymentStatus, int month, int year);

    InvoiceSummaryResponse getInvoiceSummary(String paymentStatus, Integer month, Integer year, LocalDate startDate, LocalDate endDate);

    Page<InvoiceListResponse> getInvoices(String keyword, String paymentStatus, int page, int size);

    InvoiceDetailResponse getInvoiceDetail(Long id);

    void processPayment(Long invoiceId, String paymentMethod);
}
