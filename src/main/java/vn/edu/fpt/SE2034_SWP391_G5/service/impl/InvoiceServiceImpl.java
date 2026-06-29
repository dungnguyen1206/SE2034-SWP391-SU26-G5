package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceSummaryResponse;
import vn.edu.fpt.SE2034_SWP391_G5.exception.Invoice.DataConflictException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.Invoice.InvoiceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.InvoiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.InvoiceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;

    public BigDecimal getTotalAmount(String paymentStatus, int month, int year) {
        return invoiceRepository.getTotalAmount(paymentStatus, month, year);
    }

    @Override
    public InvoiceSummaryResponse getInvoiceSummary(String paymentStatus, Integer month, Integer year, LocalDate startDate, LocalDate endDate) {

        if (month == null) {
            month = 0;
        }
        if (year == null) {
            year = 0;
        }
        //Xử lí lỗi
        if (startDate != null && endDate != null & startDate.isAfter(endDate)) {
            throw new DataConflictException("Ngày bắt đầu phải trước ngày kết thúc");
        }
        if (startDate != null && startDate.isBefore(LocalDate.parse("2001-01-01"))) {
            throw new DataConflictException("Ngày nhập không hợp lệ!");
        }
        if (endDate != null && endDate.isAfter(LocalDate.parse("2030-12-31"))) {
            throw new DataConflictException("Ngày nhập không hợp lệ!");
        }

        InvoiceSummaryResponse result = null;


        if (year == 0 && month == 0 && startDate == null && endDate == null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, LocalDate.now().atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX), null, null).orElse(null);
        }


        /*
         * Trường hợp customer nhập cả tháng năm + data range
         * */
        if ((year != 0 || month != 0) && startDate != null && endDate != null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), null, null).orElse(null);
        } else if (startDate != null && endDate == null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, startDate.atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX), null, null).orElse(null);
        } else if (startDate == null && endDate != null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, endDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), null, null).orElse(null);
        } else if (startDate == null && endDate == null) {
            if (month == 0) {
                result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, null, null, null, year).orElse(null);
            } else if (year == 0) {
                result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, null, null, month, Year.now().getValue()).orElse(null);
            } else {
                result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, null, null, month, year).orElse(null);

            }
        } else {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, LocalDate.now().atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX), null, null).orElse(null);
        }
        if (result == null) {
            result = InvoiceSummaryResponse.builder()
                    .paymentStatus(paymentStatus)
                    .paymentAmount(0L)
                    .totalPaymentAmount(BigDecimal.ZERO)
                    .build();
        }
        return result;

    }


}
