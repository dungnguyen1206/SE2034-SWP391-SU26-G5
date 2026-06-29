package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceSummaryResponse;
import vn.edu.fpt.SE2034_SWP391_G5.enums.PaymentStatus;
import vn.edu.fpt.SE2034_SWP391_G5.repository.InvoiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.InvoiceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InvoiceServiceImplTest {

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private InvoiceService invoiceService;

    @Test
    void getInvoiceSummary() {
        InvoiceSummaryResponse invoiceSummaryResponse = invoiceRepository.getTotalAmountByPaymentStatus(PaymentStatus.PENDING.toString(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay(),null,null).orElse(null);
        Assertions.assertNotNull(invoiceSummaryResponse);

    }

    @Test
    void getInvoiceDetails() {
        InvoiceSummaryResponse invoiceSummaryResponse = invoiceService.getInvoiceSummary(PaymentStatus.PENDING.toString(), null,null,LocalDate.now(),LocalDate.now());
        BigDecimal result = invoiceSummaryResponse.getTotalPaymentAmount();
        Assertions.assertEquals(BigDecimal.ZERO,result);
    }
}