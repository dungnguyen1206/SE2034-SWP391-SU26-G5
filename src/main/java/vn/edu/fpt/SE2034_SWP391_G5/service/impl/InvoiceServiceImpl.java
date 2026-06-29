package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.InvoiceRowResponse;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;

    @Override
    public BigDecimal getTotalAmount(String paymentStatus, int month, int year) {
        return invoiceRepository.getTotalAmount(paymentStatus, month, year);
    }


    /*
     *
     *
     * ALL HERE RELATED TO INVOICE SCREEN FOR MANAGER
     *
     * */
    @Override
    public InvoiceSummaryResponse getInvoiceSummary(String paymentStatus, Integer month, Integer year, LocalDate startDate, LocalDate endDate) {

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

        //  Tất cả filter đều null -> Quét trọn ngày hôm nay
        if (year == null && month == null && startDate == null && endDate == null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(
                    paymentStatus,
                    LocalDate.now().atStartOfDay(),
                    LocalDate.now().plusDays(1).atStartOfDay(),
                    null,
                    null
            ).orElse(null);
        }
        // 3. Có đủ cả khoảng ngày và tháng/năm
        else if ((year != null || month != null) && startDate != null && endDate != null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), null, null).orElse(null);
        }
        // 4. Chỉ có startDate
        else if (startDate != null && endDate == null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, startDate.atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX), null, null).orElse(null);
        }
        // 5. Chỉ có endDate
        else if (startDate == null && endDate != null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, endDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), null, null).orElse(null);
        }
        // 6. Không nhập khoảng ngày (Chỉ lọc theo Tháng / Năm)
        else if (startDate == null && endDate == null) {
            if (month == null && year != null) {
                result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, null, null, null, year).orElse(null);
            } else if (month != null && year == null) {
                result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, null, null, month, Year.now().getValue()).orElse(null);
            } else {
                result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, null, null, month, year).orElse(null);
            }
        }
        // 7. Có cả hai ngày cụ thể
        else if (startDate != null && endDate != null) {
            result = invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), null, null).orElse(null);
        }

        // Khởi tạo object rỗng nếu không tìm thấy dữ liệu
        if (result == null) {
            result = InvoiceSummaryResponse.builder()
                    .paymentStatus(paymentStatus)
                    .paymentAmount(0L)
                    .totalPaymentAmount(BigDecimal.ZERO)
                    .build();
        }
        return result;

    }

    @Override
    public Page<InvoiceRowResponse> invoiceRowResponses(Integer month, Integer year, LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
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


        if (year == 0 && month == 0 && startDate == null && endDate == null) {
            return invoiceRepository.getInvoiceInforByFilter(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay(), null, null, pageable);
        }

        /*
         * Trường hợp customer nhập cả tháng năm + data range
         * */

        if ((year != 0 || month != 0) && startDate != null && endDate != null) {
            return invoiceRepository.getInvoiceInforByFilter(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), null, null, pageable);
        } else if (startDate != null && endDate == null) {
            return invoiceRepository.getInvoiceInforByFilter(startDate.atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX), null, null, pageable);
        } else if (startDate == null && endDate != null) {
            return invoiceRepository.getInvoiceInforByFilter(endDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), null, null, pageable);
        } else if (startDate == null && endDate == null) {
            if (month == 0) {
                return invoiceRepository.getInvoiceInforByFilter(null, null, null, year, pageable);
            } else if (year == 0) {
                return invoiceRepository.getInvoiceInforByFilter(null, null, month, Year.now().getValue(), pageable);
            } else {
                return invoiceRepository.getInvoiceInforByFilter(null, null, month, year, pageable);

            }
        } else if (startDate != null && endDate != null && month == 0 && year == 0) {
            return invoiceRepository.getInvoiceInforByFilter(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), null, null, pageable);
        } else {
            return invoiceRepository.getInvoiceInforByFilter(LocalDate.now().atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX), null, null, pageable);
        }
    }


}
